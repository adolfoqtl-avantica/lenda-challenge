
class Account
  include Mongoid::Document
  include Mongoid::Timestamps::Short
  # Include default devise modules. Others available are:
  # :token_authenticatable, :confirmable,
  # :rememberable, :timeoutable and :omniauthable
  devise :database_authenticatable,
         :registerable,
         :recoverable,
         :lockable,
         :trackable,
         :validatable,
         :timeoutable

  ## Database authenticatable
  field :email,              type: String, default: ''
  field :encrypted_password, type: String, default: ''

  ## Recoverable
  field :reset_password_token,   type: String
  field :reset_password_sent_at, type: Time

  ## Rememberable
  # field :remember_created_at, type: Time

  ## Trackable
  field :sign_in_count,      type: Integer, default: 0
  field :current_sign_in_at, type: Time
  field :last_sign_in_at,    type: Time
  field :current_sign_in_ip, type: String
  field :last_sign_in_ip,    type: String

  ## Confirmable
  # field :confirmation_token,   type: String
  # field :confirmed_at,         type: Time
  # field :confirmation_sent_at, type: Time
  # field :unconfirmed_email,    type: String # Only if using reconfirmable

  ## Lockable
  field :failed_attempts, type: Integer, default: 0 # Only if lock strategy is :failed_attempts
  field :unlock_token,    type: String # Only if unlock strategy is :email or :both
  field :locked_at,       type: Time

  ## Token authenticatable
  # field :authentication_token, type: String

  # Poor man's authorization
  field :admin, type: Boolean
  field :devteam, type: Boolean

  field :mixpanel_distinct_id, type: String
  field :ga_id, type: String
  embeds_one :persistent_split_data, autobuild: true
  embeds_one :persistent_suggestion_engine_data, autobuild: true
  has_many :applications, autosave: true, order: { id: :asc }
  has_many :referrals, class_name: 'LeadManagement::Lead'
  has_many :tickets

  after_update :send_slack_notification_if_locked_out

  # def password_match?
  #   self.errors[:password] << "can't be blank" if password.blank?
    # self.errors[:password_confirmation] << "can't be blank" if password_confirmation.blank?
    # self.errors[:password_confirmation] << "does not match password" if password != password_confirmation
    # password == password_confirmation && !password.blank?
  # end
  def active_application
    applications.select(&:open?).last
  end

  def create_application(application_id)
    quote = Quote.find(application_id)
    quote.email_address = self.email
    application = quote.becomes(Application)
    self.applications << application
    application
  end

  def find_latest_application_or_create_new(application_id)
    return active_application if has_open_application?
    return applications.select(&:closed?).last if applications.present?
    create_application(application_id)
  end

  def timeout_in
    self.admin? ? 2.hours : 45.minutes
  end

  def has_open_application?
    applications.any?(&:open?)
  end

  def name
    return nil if applications.empty?
    borrower = applications
                 .sort_by(&:id)
                 .reverse
                 .map(&:borrower)
                 .detect { |b| b.name.present? }
    borrower.blank? ? nil : borrower.name
  end

  def nested_errors
    NestedErrorsPresenter.new(self).to_a
  end

  private

  def password_required?
    !persisted? || !password.nil? || !password_confirmation.nil?
  end

  def send_slack_notification_if_locked_out
    Slack::SupportNotification.notify_locked_out_account(self)
  end
end