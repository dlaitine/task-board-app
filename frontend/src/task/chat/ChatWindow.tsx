import { Box, Button, Fab, Popper, TextField, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import CancelIcon from '@mui/icons-material/Cancel';
import ChatIcon from '@mui/icons-material/Chat';
import MarkUnreadChatAltIcon from '@mui/icons-material/MarkUnreadChatAlt';
import React, { useState, useEffect } from 'react';
import { useStompClient, useSubscription } from 'react-stomp-hooks';
import { Message, NewMessage } from './chat';

const baseUrl = import.meta.env.VITE_TASK_BACKEND_BASE_URL;

const ChatWindow = () => {
  const stompClient = useStompClient();

  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [chatOpen, setChatOpen] = useState<boolean>(false);

  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState<string>('');

  const [unseenMessages, setUnseenMessages] = useState<boolean>(false);

  useEffect(() => {
    fetch(`${baseUrl}/chats`)
      .then((response) => response.json())
      .then((data) => {
        setMessages(data);
    });
  }, []);
  
  useSubscription('/topic/chat-message', (message) => {
    const chatMessage: Message = JSON.parse(message.body);
    if (!chatOpen) {
      setUnseenMessages(true);
    }
    setMessages((prevMessages) => [ ...prevMessages, chatMessage ]);
  });
  
  const toggleChat = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
    if (!chatOpen) {
      setUnseenMessages(false);
    }
    setChatOpen(prev => !prev);
  };

  const handleSendMessage = () => {
    if (newMessage.trim() !== '') {
      const message: NewMessage = {  
        userName: 'dummy',
        content: newMessage,
      };

      stompClient?.publish({
        destination: '/app/chat-message',
        body: JSON.stringify(message)
      });

      setNewMessage('');
    }
  };

  useEffect(() => {
    // Scroll to the bottom when new messages are added
    const chatWindow = document.getElementById('chat-window');
    if (chatWindow) chatWindow.scrollTop = chatWindow.scrollHeight;
  }, [messages]);

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
        { !chatOpen && unseenMessages ? <MarkUnreadChatAltIcon /> : chatOpen ? <CancelIcon /> : <ChatIcon /> }
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
        <Typography variant='h5' align='center'>Chat</Typography>
        <Grid
          container
          sx={{
            '#chat-window > p:nth-of-type(odd)': {
              background: '#EFEFEF'
            },
          }}
        >
          <Box id='chat-window' p={2} sx={{ maxHeight: '400px', minHeight: '200px', width: '100%', float: 'left', overflowY: 'auto', }}>
            {messages.map((message) => (
              <Typography key={message.id} style={{ padding: '6px', wordWrap: 'break-word', wordBreak: 'break-word' }}>
                [{new Date(message.createdAt).toLocaleString('FI-fi')}] <b>{message.userName}: </b>{message.content}
              </Typography>
            ))}
          </Box>
          <form onSubmit={(e) => {
            e.preventDefault();
            handleSendMessage();
          }}>
            <Grid container alignItems='center' style={{ marginTop: '16px' }}>
              <Grid>
                <TextField
                  sx={{
                    ".MuiOutlinedInput-root": {
                      borderRadius: '10px 0px 0px 10px',
                      width: '300px'
                    }
                  }}
                  autoComplete="false"
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  label='Type message here'
                  variant='outlined'
                  fullWidth
                />
              </Grid>
              <Grid>
                <Button
                  type='submit'
                  variant='contained'
                  color='primary'
                  sx={{
                    height: '56px',
                    borderRadius: '0px 10px 10px 0px',
                    background: '#0077B6'
                  }}>
                    Send</Button>
              </Grid>
            </Grid>
          </form>
        </Grid>
      </Popper>
    </>
  );
};

export default ChatWindow;