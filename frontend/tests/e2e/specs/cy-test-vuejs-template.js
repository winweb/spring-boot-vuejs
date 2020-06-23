// https://docs.cypress.io/api/introduction/api.html

describe('My First Test', () => {
  it('Visits the app root url', () => {
    cy.visit('/')
    cy.contains('h1', 'Welcome to your Vue.js powered Spring Boot App')
    cy.get('#nav > [href="/app/callservice"]').click();
    cy.get('button').click();
  })
})
