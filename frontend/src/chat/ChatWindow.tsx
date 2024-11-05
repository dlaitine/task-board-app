import { Fab, Popper, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import CancelIcon from '@mui/icons-material/Cancel';
import ChatIcon from '@mui/icons-material/Chat';
import MarkUnreadChatAltIcon from '@mui/icons-material/MarkUnreadChatAlt';
import React, { useState, useEffect, useContext } from 'react';
import { BoardContext } from '../context/BoardContext';
import { ChatMessageList } from './ChatMessageList';
import { ChatMessageForm } from './form/ChatMessageForm';


export const ChatWindow = () => {

  const { boardName, messages, newMessages, setNewMessages, sendMessage, } = useContext(BoardContext);

  const [ anchorEl, setAnchorEl, ] = useState<null | HTMLElement>(null);
  const [ chatOpen, setChatOpen, ] = useState<boolean>(false);

  const toggleChat = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
    if (newMessages) {
      setNewMessages(false);
    }
    setChatOpen(prev => !prev);
  };

  useEffect(() => {
    // Scroll to the bottom when new messages are added
    const chatWindow = document.getElementById('chat-window');
    if (chatWindow) {
      chatWindow.scrollTop = chatWindow.scrollHeight;
    }
  }, [ messages, ]);

  return (
    <>
      <Fab
        onClick={toggleChat}
        color="primary"
        aria-label="chat-button"
        style={{
          position: 'fixed',
          bottom: '30px',
          right: '30px',
          background: '#0077B6'
        }}>
        { !chatOpen && newMessages ? <MarkUnreadChatAltIcon /> : chatOpen ? <CancelIcon /> : <ChatIcon /> }
      </Fab>
      <Popper open={chatOpen}
        anchorEl={anchorEl}
        placement='bottom-start'
        sx={{
          height: 'auto',
          width: '400px',
          border: 'solid 1px gray',
          padding: '10px',
          borderRadius: '10px',
          background: '#FFFFFF',
          marginBottom: '20px !important'
        }}>
        <Typography variant='h5' align='center'>{boardName} Chat</Typography>
        <Grid
          container
          sx={{
            '#chat-window > p:nth-of-type(odd)': {
              background: '#EFEFEF'
            },
          }}
        >
          <ChatMessageList messages={messages} />
          <ChatMessageForm sendMessage={sendMessage} />
        </Grid>
      </Popper>
    </>
  );
};