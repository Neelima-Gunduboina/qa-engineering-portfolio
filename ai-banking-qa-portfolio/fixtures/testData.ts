/**
 * testData.ts — Typed, centralized test data factory.
 * Nothing sensitive is hardcoded: the valid login is the customer that
 * tests/auth.setup.ts registers (or PARABANK_USERNAME/PASSWORD if set).
 */

import dotenv from 'dotenv';
import { savedCredentials, uniqueUsername } from '../utils/test-user';
dotenv.config();

export interface TestData {
  /** The customer created (or configured) by tests/auth.setup.ts. Read lazily. */
  credentials: {
    validUsername: string;
    validPassword: string;
    invalidUsername: string;
    invalidPassword: string;
  };
  registration: {
    firstName: string;
    lastName: string;
    address: string;
    city: string;
    state: string;
    zipCode: string;
    phone: string;
    ssn: string;
    uniqueUsername: () => string;
    password: string;
  };
  transfer: {
    smallAmount: string;
    largeAmount: string;
  };
  billPayment: {
    payeeName: string;
    address: string;
    city: string;
    state: string;
    zipCode: string;
    phone: string;
    accountNumber: string;
    amount: string;
  };
}

export function makeTestData(): TestData {
  return {
    credentials: {
      get validUsername() {
        return savedCredentials().username;
      },
      get validPassword() {
        return savedCredentials().password;
      },
      invalidUsername: 'invalid_user_xyz_9999',
      invalidPassword: 'wrongpass123',
    },
    registration: {
      firstName: 'Neelima',
      lastName: 'Gunduboina',
      address: '123 Test Street',
      city: 'London',
      state: 'LN',
      zipCode: 'EC1A 1BB',
      phone: '07700900000',
      ssn: '123-45-6789',
      // Random and at most 20 characters: ParaBank reports longer new
      // usernames as "already exists" (DEF-005).
      uniqueUsername: () => uniqueUsername('reg'),
      password: 'SecurePass@2026',
    },
    transfer: {
      smallAmount: '10.00',
      largeAmount: '999999.00',
    },
    billPayment: {
      payeeName: 'BT Broadband',
      address: '456 Payee Lane',
      city: 'Manchester',
      state: 'MN',
      zipCode: 'M1 1AE',
      phone: '01619990000',
      accountNumber: '12345',
      amount: '50.00',
    },
  };
}
