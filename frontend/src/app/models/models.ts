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

export enum PlayMode {
  Default,
  Single,
  Playlist
}

export interface Track {
  id: number,
  name: string,
  duration: number,
  views: number,
  publishedDate: string,
  fileMongoRef: String
  genres: any[],
  artists: User[]
}

export interface Collection {
  id: number,
  name: string,
  type: string,
  imageMongoRef: string,
  duration: number,
  views: number,
  publishedDate: string,
  tracks: Track[],
  users: User[],
  owner: User
}

export interface SelectedSongInCollectionEvent {
  collection: Collection,
  selectedTrackId: number,
  selectedTrackIndex: number
}

export interface playingSongFromCollectionEvent {
  selectedTrackId: number,
  collectionId: number;
}

export interface pausingSongFromCollectionEvent {
  selectedTrackId: number,
  collectionId: number;
}

export interface PlayingSongEvent {
  selectedTrackId: number,
  collectionId: number;
}
export interface PausingSongEvent {
  selectedTrackId: number,
  collectionId: number;
}

export interface PlayCollectionEvent {
  collectionId: number
}

export interface CollectionRequest {
  name: string,
  image: Blob | null,
  type: string,
}
