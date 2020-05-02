import { ReportReason } from './enums';
import { User } from './User';

export interface Report {
  reported: User;
  reportedBy: User;
  timeStamp: Date;
  reportReasons: string[];
}