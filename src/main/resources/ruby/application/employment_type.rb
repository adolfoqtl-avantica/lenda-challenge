module EmploymentType
  SALARIED = :salaried
  SELF_EMPLOYED = :self_employed
  RETIRED = :retired
  UNEMPLOYED = :unemployed
  FULL_TIME_STUDENT = :full_time_student
  EMPLOYED_PART_TIME = :employed_part_time
  HOME_MANAGER = :home_manager
  OTHER = :other

  VALUES = [ SALARIED, SELF_EMPLOYED, RETIRED, UNEMPLOYED, FULL_TIME_STUDENT, EMPLOYED_PART_TIME, HOME_MANAGER, OTHER ]
  NAMES = VALUES.map { |v| I18n.t("employment_type.#{v}") }
  MAP = NAMES.zip(VALUES)
  
  NON_EMPLOYED_TYPES = [RETIRED, UNEMPLOYED, FULL_TIME_STUDENT, HOME_MANAGER]

  def self.name_for(value)
    I18n.t("employment_type.#{value}")
  end
  
  def self.key_for(name)
    Hash[MAP][name]
  end
end
