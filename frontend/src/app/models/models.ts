export interface SignUpRequest {
  displayName: string,
  email: string,
  password: string,
  firstName: string,
  lastName: string
}

export interface SignResponse {
  type: string
  token: string
  user: User
}

export interface SignInRequest {
  email: string,
  password: string,
}

export interface UpdateRequest {
  firstName?: string,
  lastName?: string,
  displayName?: string
}

export interface UserExistsByRequest {
  by: string
}

export interface UserExistsByResponse {
  exists: boolean
}

export interface ConfirmEmailResponse {
  success: boolean;
}

export interface PasswordResetPinToKeyResponse {
  key: string;
}

export interface PasswordChangeRequest {
  oldPassword: string;
  newPassword: string;
  repeatedNewPassword: string;
}

export interface GeoLocation {
  city: string;
  country: string;
  continent: string;
  latitude: number;
  longitude: number;
  radius: number;
}

export interface ActivityResponse {
 activity: string;
 occurrenceDate: Date;
 location: GeoLocation;
 ip: string;
 hiddenIp: boolean;
}

export interface ApiError {
  statusCode: number,
  timestamp: number,
  message: string
}

export interface User {
  id: number,
  firstName: string
  lastName: string
  displayName: string,
  email: string,
  emailConfirmed: boolean,
}

export interface Collection {
  id: number,
  name: string,
  type: string,
  avatarPath: string,
  tracks: any[],
  users: User[]
}