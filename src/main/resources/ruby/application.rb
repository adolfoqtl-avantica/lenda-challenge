class Application < Quote
  include StepUrlHelper
  LoanAppGen = LoanApplicationGenerator
  belongs_to :account
  delegate :ga_id, :sign_in_count, :current_sign_in_at, :last_sign_in_ip, :last_sign_in_at, to: :account, prefix: true
  has_many :background_jobs, order: { id: :asc }
  embeds_one :property_address, class_name: 'SubjectProperty', as: :addressable
  embeds_one :primary_residence, autobuild: true
  embeds_many :additional_properties, class_name: 'Property'
  embeds_one  :mailing_address, class_name: 'Address', as: :addressable, autobuild: true
  embeds_many :former_addresses, class_name: 'Residency', as: :addressable
  embeds_one :borrower, autobuild: true
  embeds_one :co_borrower, class_name: 'Borrower'
  has_one :credit_check, class_name: 'Credit::CreditCheck', autobuild: true, autosave: true
  embeds_many :stage_history, class_name: 'ApplicationStageActivityEntry', inverse_of: :stage_history
  embeds_many :documents
  embeds_many :disclosures, class_name: 'Disclosures'
  embeds_many :payoffs, class_name: 'Settlement::Payoff'

  has_many :change_events, order: { id: :asc }

  scope :recent, -> { where(:created_at.gte => 30.days.ago).desc(:id) }

  accepts_nested_attributes_for :stage_history
  
  accepts_nested_attributes_for :primary_residence,
                                allow_destroy: true,
                                reject_if: :property_is_primary_residence?

  accepts_nested_attributes_for :property_address,
                                :mailing_address,
                                :additional_properties,
                                :former_addresses,
                                allow_destroy: true,
                                reject_if: proc { |attributes| attributes.all? { |k,v| v.blank? || ['_id', '_type', 'zip_code', 'state'].include?(k) } }
                                # we initialize the application with passed in parameters,
                                # but need to allow the user to go backwards and forwards to the review quote page
                                # to make a new selection if they choose. This, and then the redundant
                                # @quote.save(validate: false) in the quote controller
                                # make sure that we never see an issue for validation between those two pages.
 
  accepts_nested_attributes_for :borrower, :co_borrower, reject_if: :all_blank

  # TODO: move fields that don't apply to quotes to here
  field :mailing_address_different, type: Boolean
  field :owns_additional_properties, type: Boolean
  field :borrower_not_signed, type: Boolean, default: true

  field :permission_for_credit_check, type: Boolean
  
  field :signed_disclosures_on, type: DateTime

  field :trid_completed_at, type: DateTime
  field :temporary_login_step, type: String
  
  field :status, type: String, default: Status::OPEN

  field :salesforce_lead_id, type: String
  field :salesforce_opportunity_id, type: String
  field :team_members, type: Array, default: []
  
  # About Home -- Purchase-specific
  field :property_identified, type: Boolean
  field :offer_accepted, type: Boolean
  field :working_with_realtor, type: Boolean

  # Assets -- Purchase-specific
  field :using_gift_for_down_payment, type: Boolean

  delegate :interest_rate, :locked?, :term, to: :mortgage
  
  field :first_time_home_buyer, type: Boolean

  before_validation :clear_mailing_address
  before_validation :clear_additional_properties

  state_machine :step do
    state :unified_rate_quote

    state :account

    state :pre_qualified

    state :declined

    state :about_you do
      validates_presence_of :borrower
      validates_presence_of :applying_as, message: "Please select #{ApplicationType::VALUES.join(' or ')}"
      validates_presence_of :first_time_home_buyer, if: :purchase?
    end

    state :about_co_borrower do
      validate :co_borrower_unique_email
    end
    
    state :about_home do
      validates_presence_of :property_address, if: :refinancing_or_purchase_with_offer_accepted?
      validates_presence_of :purchase_cost, if: :refinance?
      validates_presence_of :purchase_year, if: :refinance?
      validates_presence_of :year_built, if: :refinance?
      validates_presence_of :primary_residence unless :property_is_primary_residence?
      validates_presence_of :user_supplied_county_id, if: :property_address_multi_county?
      validates_presence_of :mailing_address, if: :mailing_address_different
      validates_presence_of :monthly_hoa_dues, if: :refinancing_and_property_is_condo_or_townhome?
      validates_presence_of :property_identified, if: :purchase?
      validates_presence_of :offer_accepted, if: :purchase_with_property_identified?
      validates_presence_of :working_with_realtor, if: :purchase_with_property_identified?
      include AddressHistoryValidations
    end

    state :employment_and_income

    state :co_borrower_employment_and_income

    state :assets do
      validates_presence_of :using_gift_for_down_payment, if: :purchase?
    end
    state :government_monitoring_questions

    state :declarations

    state :credit_check_permission do
      validate :permission_for_credit_check
      validate :borrower_unique_ssn
      validate :co_borrower_unique_ssn
    end
    state :finish do
      # need this step because uses a finish step to redirect
      # otherwise #set_furthest_step hits an error when it can't find finish
    end
    state :credit_check

    state :sign

    state :co_borrower_sign

    state :upload

    state :complete
  end

  def self.state_machine_steps
    exclusions = [:homeowner, :review_quote, :account, :finish]
    Application.state_machines[:step].states.map(&:name) - exclusions
  end
  
  def all_addresses
    addresses = [self.property_address]
    addresses += self.additional_properties.map(&:address)
    addresses += self.former_addresses
    addresses << self.primary_residence.address if self.primary_residence.address.persisted?
    addresses.uniq.compact
  end

  # Note:
  # This method is a hack so we can allow admins to 
  # "delete" a co-borrower without actually deleting them.
  # This will return the co_borrower -- if it exists --
  # when an application.multi_borrower? If an application
  # is NOT multi_borrower, then return nil regardless of 
  # whether or not the actual co_borrower exists in our database.
  alias_method :original_co_borrower, :co_borrower
  # Note 2:
  # Alias methods are required to prevent stack overflowing
  def co_borrower
    multi_borrower? ? original_co_borrower : nil
  end

  def floated?
    !locked?
  end

  def nested_errors
    NestedErrorsPresenter.new(self).to_a
  end
  
  def borrower_names
    borrowers.map(&:name).join(' and ')
  end

  def property_or_primary_residence_address
    property_is_primary_residence? ? property_address : primary_residence.address
  end

  def property_or_mailing_address
    mailing_address_different ? mailing_address : property_address
  end

  def changelog
    change_events.reverse
  end

  def loan_amount
    mortgage.loan_amount || super
  end

  def payoff_amount
    payoffs? ? payoffs.map(&:amount).sum : mortgage_balance
  end

  def requires_assets?
    return false if purchase? && selected_loantek_rate.blank?
    mortgage && cash_to_close > 0.0
  end
  
  def cash_to_close
    mortgage.settlement_cost.settlement.amount
  end
  
  def cash_to_close_covered?
    total_assets = combined_assets.map(&:amount_in_account).sum
    total_assets >= cash_to_close 
  end

  def borrower_email=(email)
    borrower.email = email
  end

  def borrower_email
    borrower.email
  end 

  def update_salesforce(options = {})
    SalesforceWorker::SaveWorker.queue(token, options) if account.try(:email?)
  end

  def property_is_primary_residence?
    return false if purchase?
    occupancy_of_home == OccupancyOfHome::Primary
  end
  
  def refinancing_and_property_is_condo_or_townhome?
    refinance? && (condo? || townhome?)
  end
  
  def transition_to(new_stage, admin_name)
    return if new_stage.blank? || stage == new_stage
    stage_history << ApplicationStageActivityEntry.new(
      stage: new_stage,
      changed_by: admin_name,
      changed_at: Time.now
    )
  end
  
  def open?
    status == Status::OPEN
  end

  def closed?
    status == Status::CLOSED
  end
  
  def stage
    stage_history.last.try(:stage) || ApplicationStage.pending
  end
  
  def stage_activity
    stage_history.desc(:changed_at)
  end

  def opportunity?
    furthest_step == 'complete' || (stage && stage != ApplicationStage.pending)
  end
  
  def rental_properties
    additional_properties.select(&:is_rental_property?)
  end
  
  def investment_properties
    properties = rental_properties
    properties << property_address if property_is_investment_property?
    properties
  end

  def compliance_regime
    if Lenda::TRID.enabled? && !signed_pre_trid?
      Lenda::ComplianceRegime::TRID
    else
      Lenda::ComplianceRegime::NON_TRID
    end
  end

  alias_method :trid_complete, :trid_completed_at?

  def set_trid_completed_at
    if !trid_complete && [
      borrower.first_name,
      borrower.last_name,
      borrower.ssn,
      (annual_income || borrower.annual_gross_income),
      value_of_home,
      loan_amount,
      property_address.try(:address_1),
      property_address.try(:city),
      property_address.try(:state),
      property_address.try(:zip_code)
    ].all?(&:present?)
      update_attribute(:trid_completed_at, DateTime.now)
    end
  end

  def borrowers
    borrowers = []
    borrowers << borrower
    borrowers << co_borrower if multi_borrower?
    borrowers.compact
  end

  def document(document_type, **document_options)
    DocumentList.new(self).document(document_type, **document_options)
  end

  def employments_within_years(years)
    employments.select { |e| years.any? { |year| e.active_in?(year) } }
  end

  def employments
    borrowers.map(&:employments).flatten
  end
  
  def loan_advisor
    loan_advisor_hash = team_members.detect { |member| member[:role] == User::Role::LOAN_ADVISOR }
    return UnknownLoanAdvisor.new if loan_advisor_hash.blank?
    LoanAdvisor.new(loan_advisor_hash[:name], loan_advisor_hash[:email])
  end
  
  def originator
    Originator.instance
  end

  def disclosures_by_job_id(background_job_id)
    disclosures.find_by(background_job_id: background_job_id)
  end

  def selected_rate_id
    return nil if selected_loantek_rate.blank?

    latest_rate_group = historical_loantek_rates.map(&:selected_at).uniq.max.to_i
    historical_rates = historical_loantek_rates.select { |r| r.selected_at.to_i == latest_rate_group }
    (historical_rates.detect { |r| r.name == selected_loantek_rate.name }).id.to_s
  end

  def can_pull_credit?
    return false unless borrower
    valid_borrower = borrower && borrower.ssn.present? && permission_for_credit_check?
    if multi_borrower?
      valid_borrower && co_borrower && co_borrower.ssn.present?
    else
      valid_borrower
    end
  end

  def select_rate!(loantek_rate)
    self.step = Application::Step::RATE_SELECTED
    self.selected_loantek_rate = loantek_rate.clone
    build_mortgage_from_rate(loantek_rate)
    archive_latest_loantek_rates
    self.selected_loantek_rate
  end

  def has_selected_rate?
    return !selected_loantek_rate.blank?
  end

  def real_estate_owned
    properties = []
    properties << build_property_from_subject_property if refinancing_or_purchase_with_offer_accepted?
    if !property_is_primary_residence? && primary_residence.address.own
      properties << primary_residence
    end
    properties += additional_properties
    properties
  end

  def property_address_converted_to_property
    @property_address_converted_to_property ||= build_property_from_subject_property
  end

  def build_payoffs_from_liabilities
    liabilities = credit_check.primary_credit_report.liabilities.select(&:payoff)
    self.payoffs = liabilities.map{ |l| Settlement::Payoff.from_liability(l) }
  end
  
  def borrower_signed
    !borrower_not_signed
  end

  def invalid_county_selected
    property_address_multi_county? && user_supplied_county_id.blank?
  end

  def property_address_multi_county?
    property_address && property_address.multi_county?
  end

  def purchase_with_offer_accepted?
    purchase? && offer_accepted?
  end

  def to_sanitized_json
    touch_mortgage_and_timeline
    json = JSON.parse(self.to_json)
    json['borrower']['ssn'] = 'xxx-xx-xxxx' if json['borrower']
    json['co_borrower']['ssn'] = 'xxx-xx-xxxx' if json['co_borrower']
    json.to_json
  end

  def prequalify!
    # First, check the application data to see if it passes initial rule set
    unless initial_rules_result.success?
      errors = initial_rules_result[:errors]
      errors = errors.messages if errors.respond_to?(:messages)
      return decline_prequalify(errors)
    end

    # The self passed initial rules, and is eligible to check rates.
    get_loantek_rates

    if rates.blank? 
      # Loantek was up, but returned no rates for our application.
      decline_prequalify(rates_unavailable: 'No rates available')
    elsif subsequent_rules_results.error? 
      # Rules engine failed on DTI.
      decline_prequalify(subsequent_rules_results[:errors])
    else
      update_attribute(:step, Application::Step::PRE_QUALIFIED)
      update_attribute(:prequalified_status, PrequalifiedStatuses::APPROVED)
      update_attribute(:prequalified_errors, {})
    end
  end

  def reset_post_login_url!
    return unless post_login_url
    steps_to_check = application_build_steps.dup
    steps_to_check += [:results, :rate_selected, :unified_rate_quote] if refinance?
    update_attribute(:post_login_url, nil) if steps_to_check.include?(step.to_sym)
    true
  end

  private

  def decline_prequalify(errors)
    update_attribute(:step, Application::Step::DECLINED)
    update_attribute(:prequalified_status, PrequalifiedStatuses::DENIED)
    update_attribute(:prequalified_errors, errors)
    false
  end

  def initial_rules_result
    submit_quote_use_case = BorrowerApplication::SubmitQuoteUseCase.from_application(self)
    submit_quote_use_case.quotes_decliner = quote_initial_rule_set
    @initial_rules_results ||= submit_quote_use_case.execute
  end

  def quote_after_submit_rule_set
    @quote_after_submit_rule_set ||= quotes_decliner(RulesEngine.quote_after_submit_rule_set)
  end

  def quote_initial_rule_set
    @quote_initial_rule_set ||= quotes_decliner(RulesEngine.quote_initial_rule_set)
  end

  def quotes_decliner(version)
    Application::QuotesDecliner.new(version)
  end

  def subsequent_rules_results
    query = RenderRatesQuery.new(quote: self, quotes_decliner: quote_after_submit_rule_set)
    @subsequent_rules_results ||= query.execute
  end

  def build_mortgage_from_rate(loantek_rate)
    self.mortgage.interest_rate = loantek_rate.rate
    self.mortgage.term = loantek_rate.term
    self.mortgage.cash_out = self.cash_out?
    self.mortgage.financed = loantek_rate.financed?
    self.mortgage.escrow = self.escrow
    self.mortgage.loan_amount = loantek_rate.loan_amount
    self.mortgage.transaction_costs = loantek_rate.settlement_cost.static_transaction_costs
  end

  def archive_latest_loantek_rates
    rates = loantek_rates.map(&:clone)
    timestamp = Time.now
    rates.each { |r| r.selected_at = timestamp }
    self.historical_loantek_rates << rates
  end

  def build_property_from_subject_property
    Property.new(
      address: property_address,
      estimated_value: value_of_home,
      property_type: Property::PROPERTY_TYPES[property_type],
      property_disposition: PropertyDisposition::Retained,
      gross_rental_income: nil,
      mortgage_payments: mortgage.p_and_i,
      insurance_maintenance_taxes: mortgage.monthly_property_taxes + mortgage.monthly_hoi,
      liens_amount: mortgage_balance,
      is_rental_property: property_is_investment_property?,
      escrowed: mortgage.escrow,
    )
  end

  def clear_mailing_address
    mailing_address.delete unless mailing_address_different
  end

  def clear_additional_properties
    additional_properties.delete_all unless owns_additional_properties
  end

  def borrower_unique_ssn
    check_ssn_uniqueness(borrower)
  end

  def co_borrower_unique_ssn
    check_ssn_uniqueness(co_borrower)
  end

  def co_borrower_unique_email
    if multi_borrower? && co_borrower.email == borrower_email
      errors.add(:co_borrower_email, I18n.t('custom.errors.application.co_borrower_email'))
      co_borrower.errors.add(:email, I18n.t('custom.errors.borrower.attributes.email.unique'))
    end
  end

  def check_ssn_uniqueness(current_borrower)
    if multi_borrower? && co_borrower.andand.ssn == borrower.ssn
      # this error on the application is not displayed but it is necessary
      #   for validation to register that the update failed
      if errors[:borrower_ssn].empty?
        errors.add(:borrower_ssn, 'Both borrowers must have unique Social Security Numbers')
      end
      
      current_borrower.errors.add(:ssn, "Must be different than your co-borrower's Social Security Number")
    end
  end

  def signed_pre_trid?
    if signed_disclosures_on == nil
      false
    else
      Lenda::TRID::COMPLIANCE_DATE > signed_disclosures_on
    end
  end

  def purchase_with_property_identified?
    purchase? && property_identified?
  end

  def refinancing_or_purchase_with_offer_accepted?
    refinance? || purchase_with_offer_accepted?
  end

  def touch_mortgage_and_timeline
    # Despite mortgage + timeline being autobuilt, they need
    # to be 'touched' in order to initialize methods.
    # Without this, calling .to_json on an app created via API will fail.
    mortgage && mortgage.timeline
  end
end
