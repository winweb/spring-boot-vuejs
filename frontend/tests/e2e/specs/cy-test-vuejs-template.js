// https://docs.cypress.io/api/introduction/api.html

describe('My First Test', () => {
  it('Visits the app root url', () => {
    cy.visit('/')
    cy.get('img').should('have.attr', 'src').should('include','spring-boot-vuejs-logo')
    cy.contains('h1', 'Welcome to your Vue.js powered Spring Boot App')
  })

  it('Visits the service page', () => {
    cy.get('#nav > [href="/app/callservice"]').click();
    cy.get('button').click();
    cy.contains("h4","Backend response: Hello from Spring Boot Backend!");
  })

  it('Visits the bootstrap page', () => {
    cy.get('#nav > [href="/app/bootstrap"]').click();
  })

  it('Visits the user page', () => {
    cy.get('#nav > [href="/app/user"]').click();

    cy.get('[placeholder="first name"]').clear();
    cy.get('[placeholder="first name"]').type("user")
        .should('have.value',"user")

    cy.get('[placeholder="last name"]').clear();
    cy.get('[placeholder="last name"]').type("pass")
        .should('have.value',"pass")

    cy.get('button').click();

    cy.get('.user > :nth-child(7)').click();
    cy.get('h4').contains("Retrieved User pass user");
  })
})
