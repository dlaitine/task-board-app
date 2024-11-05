import { Box, } from "@mui/material";
import { Message } from "./chat"
import { ChatMessageListItem } from "./ChatMessageListItem";
import { memo } from "react";

interface ChatMessageListProps {
  messages: Message[]
};

export const ChatMessageList = memo(({ messages, }: ChatMessageListProps) => (
  <Box id='chat-window' p={2} sx={{ maxHeight: '400px', minHeight: '200px', width: '100%', float: 'left', overflowY: 'auto', }}>
    {messages.map((message) => (
      <ChatMessageListItem key={message.id} message={message} />
    ))}
  </Box>
));