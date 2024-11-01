export interface NewMessage {
  username: string;
  content: string;
}

export interface Message {
  id: number;
  username: string;
  content: string;
  created_at: number;
}