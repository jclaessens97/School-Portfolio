import { Role } from './Role';

export class User {
  userName: string;
  userId: string;
  created: Date;
  roles: Role[];
  wins: number; // ??
}
