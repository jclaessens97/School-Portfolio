// import { getGreeting } from '../support/app.po';
import { Chance} from 'chance';
import {equal} from 'assert';


const chance = new Chance();

describe('cluedo', () => {
  beforeEach(() => cy.visit('/'));

  describe('Register', () => {
    it('Register happy path', () => {
      const email = chance.email();
      const username = chance.string({ length: 5 });

      cy.visit('/');
      cy.get('#mat-tab-label-0-1').click();
      cy.get('[formControlName="email"]')
        .type(email);
      // cy.get('[formControlName="username"]')
      //   .type('fake')
      //   .should('have.value', 'fake');
      cy.get('#registerUsername').type(username);
      cy.get('[formControlName="password"]')
        .type('Admin1')
        .should('have.value', 'Admin1');

      cy.get('.registerBtn').click();
      cy.wait(500);
      cy.get('alert').children()
        .should('contain', 'You have registered succefully.');

      cy.get('#loginUsername').type(username);
      cy.get('[formControlName="password"]')
        .type('Admin1');
      cy.get('.loginBtn').click();
      cy.url().should('include', '/lobbies');

    });

    it('Register no email', () => {
      const email = '';
      const username = chance.string({ length: 5 });
      // const fixture = TestBed.createComponent(RegisterComponent);
      // const userService = fixture.debugElement.injector.get(UserService);

      cy.visit('/');
      cy.get('#mat-tab-label-0-1').click();

      cy.get('[formControlName="email"]')
        .focus().blur();

      cy.get('#registerUsername').type(username);
      cy.get('#registerPassword')
        .type('Admin1');

      cy.get('.registerBtn').click();
      cy.get('input:invalid').should('have.length', 1);
      cy.get('mat-error').children().should('contain', 'Email is required');

      // cy.spy(userService, 'register');

      // expect(userService.register).to.be.called;

    });

    it('Register short password', () => {
      const email = chance.email();
      const username = chance.string({ length: 5 });

      cy.visit('/');
      cy.get('#mat-tab-label-0-1').click();

      cy.get('[formControlName="email"]')
        .type(email);

      cy.get('#registerUsername').type(username);
      cy.get('#registerPassword')
        .type('Ad');

      cy.get('.registerBtn').click();
      // cy.get('input:invalid').should('have.length', 1);
      cy.get('mat-error').children().should('contain', 'Password must be at least 6 characters');

    });


    it('Register same email', () => {
      const email = chance.email();
      const username = chance.string({ length: 5 });

      cy.visit('/');
      cy.get('#mat-tab-label-0-1').click();

      cy.get('[formControlName="email"]')
        .type(email);

      cy.get('#registerUsername').type(username);
      cy.get('#registerPassword')
        .type('Admin1');

      cy.get('.registerBtn').click();

      cy.visit('/');
      cy.get('#mat-tab-label-0-1').click();

      cy.get('[formControlName="email"]')
        .type(email);

      cy.get('#registerUsername').type(username);
      cy.get('#registerPassword')
        .type('Admin1');

      cy.get('.registerBtn').click();

      cy.get('alert').children()
        .should('contain', 'USERNAME_EMAIL_EXISTS');

    });

  });
});
