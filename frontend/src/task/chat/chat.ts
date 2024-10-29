export interface NewMessage {
  userName: string;
  content: string;
}

export interface Message {
  id: number;
  userName: string;
  content: string;
  createdAt: Date;
}