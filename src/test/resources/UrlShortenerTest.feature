Feature: URL Shortener

  Scenario: A User can create his account using a non-existing AccountId
    Given I am user
    When I try to create account with a non-existing accountid
    Then An account with the given accountId is created with its auto-generated password returned

  Scenario: A User cannot create a new account using an invalid AccountId
    Given I am user
    When I try to create account with an invalid accountid
    Then Account can not be created and an invalid accountid message returned

  Scenario: A User cannot create a new account using an existing AccountId
    Given I am user
    When I try to create account with an existing accountid
    Then Account can not be created and an existing accountid message returned


  Scenario: An authorized account holder can register a valid URL for shortening
    Given I am an authorized account holder with a basic authorization token
    When I try to register a valid url
    Then the url is registered and shortened url returned

  Scenario: An unauthorized user cannot register an URL
    Given I do not have an authorized account holder
    When I try to register an url
    Then an invalid auth token message is returned

  Scenario: An authorized account holder cannot register an invalid URL
    Given I am an authorized account holder with a basic authorization token
    When I try to register an invalid url
    Then the url is not registered and an invalid url message returned

  Scenario: An authorized account holder cannot register an URL
    Given I am an authorized account holder with a basic authorization token
    When I try to register an already registered url
    Then the url is not registered and an url already registered message returned


  Scenario: An authorized account holder can retrieve that account's statistics
    Given I am an authorized account holder with a basic authorization token
    When I try to access the account statistics
    Then the account statistics are returned

  Scenario: An unauthorized user cannot retrieve any account's statistics
    Given I do not have an authorized account holder
    When I try to access the account statistics
    Then an invalid auth token message is returned

  Scenario: An authorized account holder cannot retrieve a different account's statistics
    Given I am an authorized account holder with a basic authorization token
    When I try to access different account statistics
    Then an unauthorized access message is returned

