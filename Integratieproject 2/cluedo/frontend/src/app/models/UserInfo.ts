import { Role } from "./Role";
export class UserInfo {
  public email: string;
  public username: string;
  verified: boolean;
  roles: Role[];
}
