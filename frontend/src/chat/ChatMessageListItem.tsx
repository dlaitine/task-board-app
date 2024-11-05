import { Typography } from "@mui/material";
import { Message } from "./chat"

interface ChatMessageListItemProps {
  message: Message;
};

export const ChatMessageListItem = ({ message, }: ChatMessageListItemProps) => (
  <Typography style={{ padding: '6px', }}>
    [{new Date(message.created_at).toLocaleString('FI-fi')}] <b>{message.username}: </b>{message.content}
  </Typography>
);