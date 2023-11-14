 [[text:

Feature: List all pets

  Scenario Outline: List all pets
    Given I have a valid API request
    When I send a GET request to "/pets"
    Then I should receive a response with status code <status_code>
    And the response should contain a list of pets

    Examples:
    | status_code |
    | 200         |


Feature: Create a pet

  Scenario Outline: Create a pet
    Given I have a valid API request
    When I send a POST request to "/pets"
    Then I should receive a response with status code <status_code>
    And the response should contain a pet

    Examples:
    | status_code |
    | 201         |


Feature: Info for a specific pet

  Scenario Outline: Info for a specific pet
    Given I have a valid API request
    When I send a GET request to "/pets/{petId}"
    Then I should receive a response with status code <status_code>
    And the response should contain a pet

    Examples:
    | status_code |
    | 200         |, index:0, logprobs:null, finish_reason:stop]] [[text:
Scenario Outline: List all pets
Given the user is on the petstore page
When the user clicks on the list pets button
Then the user should be able to view the list of pets

Examples:
| limit |
| 10    |
| 20    |
| 30    |

Scenario Outline: Create a pet
Given the user is on the petstore page
When the user clicks on the create pet button
Then the pet should be created successfully

Examples:
| petId |
| 1     |
| 2     |
| 3     |

Scenario Outline: Show pet by id
Given the user is on the petstore page
When the user clicks on the show pet by id button
Then the user should be able to view the pet details

Examples:
| petId |
| 1     |
| 2     |
| 3     |, index:0, logprobs:null, finish_reason:stop]] [[text:

Feature: List all pets

  Scenario Outline: List all pets
    Given I have a valid API request
    When I send a GET request to "/pets"
    Then I should receive a response with status code <status_code>
    And the response should contain a list of pets

    Examples:
    | status_code |
    | 200         |, index:0, logprobs:null, finish_reason:stop]] [[text:

Scenario Outline: Positive Scenario for List Pets

Given the user is on the petstore page
When the user clicks on the list pets option
Then the user should be able to view the list of pets with the limit of 100 items

Scenario Outline: Positive Scenario for Create Pets

Given the user is on the petstore page
When the user clicks on the create pets option
Then the user should be able to create a pet with the given details

Scenario Outline: Positive Scenario for Show Pet By Id

Given the user is on the petstore page
When the user clicks on the show pet by id option
Then the user should be able to view the pet details with the given pet id, index:0, logprobs:null, finish_reason:stop]] [[text:

Feature: List all pets

  Scenario Outline: List all pets with negative scenarios
    Given I have a limit parameter set to <limit>
    When I send a GET request to "/pets"
    Then the response status code should be <status_code>

    Examples:
    | limit | status_code |
    | -1    | 400         |
    | 101   | 400         |, index:0, logprobs:null, finish_reason:stop]] [[text:

Scenario Outline: Negative Scenario for List Pets

Given the user is on the list pets page
When the user enters a limit value greater than 100
Then the user should receive an error message

Examples:
| limit |
| 101   |, index:0, logprobs:null, finish_reason:stop]] [[text:

Scenario Outline: List all pets
Given the user is on the petstore page
When the user clicks on the "List all pets" button
Then the user should be able to view the list of pets

Examples:
| limit |
| 10    |
| 20    |
| 30    |

Scenario Outline: Create a pet
Given the user is on the petstore page
When the user clicks on the "Create a pet" button
Then the user should be able to create a pet

Examples:
| petId |
| 1     |
| 2     |
| 3     |

Scenario Outline: Show pet by id
Given the user is on the petstore page
When the user clicks on the "Show pet by id" button
Then the user should be able to view the pet details

Examples:
| petId |
| 1     |
| 2     |
| 3     |, index:0, logprobs:null, finish_reason:stop]] [[text:

Feature: Security Scenarios

  Scenario Outline: Verify the security of the petstore
    Given I am a user
    When I access the petstore
    Then I should be able to view the list of pets
    And I should be able to create a pet
    And I should be able to view the info of a specific pet
    And I should not be able to access more than 100 items at one time
    And I should not be able to access the petstore with an invalid request
    And I should not be able to access the petstore with an unexpected error

    Examples:
      | limit |
      | 10    |
      | 50    |
      | 100   |, index:0, logprobs:null, finish_reason:stop]] [[text:

Scenario Outline: Security Scenarios for Swagger Petstore

Given the user is on the Swagger Petstore page
When the user attempts to access the page
Then the user should be authenticated with a valid username and password

Examples:
| username | password |
| user1   | pass1    |
| user2   | pass2    |
| user3   | pass3    |, index:0, logprobs:null, finish_reason:stop]]