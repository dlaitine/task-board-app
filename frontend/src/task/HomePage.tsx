import { Box, Button, Divider, FormControlLabel, FormGroup, IconButton, List, ListItem, ListItemButton, ListItemText, Stack, Switch, TextField, Tooltip, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import InfoIcon from '@mui/icons-material/Info';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import { useContext, useEffect, useState } from 'react';
import { Board, NewBoard } from './board';
import { useNavigate } from 'react-router-dom';
import { LoginContext } from './context/LoginContext';
import { NotificationContext } from './context/NotificationContext';
import LoadingSpinner from './LoadingSpinner';

const baseUrl = import.meta.env.VITE_TASK_BACKEND_BASE_URL;

export const HomePage = () => {
  const navigate = useNavigate();

  const [ boards, setBoards, ] = useState<Board[]>([]);
  const [ newBoardName, setNewBoardName, ] = useState('');
  const [ isPrivate, setIsPrivate, ] = useState(false);

  const [ boardNameError, setBoardNameError, ] = useState<string>('');

  const [ loading, setLoading, ] = useState<boolean>(false);

  const [ submitIsClicked, setSubmitIsClicked, ] = useState<boolean>(false);

  const { setMessage, setSeverity, } = useContext(NotificationContext);
  const { username, } = useContext(LoginContext);


  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    fetch(`${baseUrl}/boards`)
      .then((response) => {
        if (!response.ok) {
          throw Error('Board fetching failed')
        }
        return response.json();
      })
      .then((data) => {
        setBoards(data);
      })
      .finally(() => {
        setLoading(false);
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setSeverity('error');
          setMessage(error.message);
        }
      });

    return () => {
      controller.abort(); // Cancel the fetch request when the component is unmounted
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleCreateBoard = () => {
    setSubmitIsClicked(true);
    if (newBoardName.trim() === '') {
      setBoardNameError('Required');
      setSubmitIsClicked(false);
      return;
    }

    const newBoard: NewBoard = {
      name: newBoardName,
      is_private: isPrivate,
    };

    fetch(`${baseUrl}/boards`, {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newBoard)
    })
    .then((response) => {
      if (!response.ok) {
        throw Error('Board creation failed')
      }
      return response;
    })
    .then((response) => response.json())
    .then((data) => {
      setSeverity('success');
      setMessage('New board \'' + newBoardName + '\' created successfully');
      navigate(`/${data.id}`)
    })
    .finally(() => {
      setSubmitIsClicked(false);
    })
    .catch((error) => {
      setSeverity('error');
      setMessage(error.message);
    });
  };

  return (
    <>
      <Stack padding={4} display='flex' justifyContent='center' alignItems='center'>
        <Typography variant='h4'>Welcome, {username}!</Typography>
        <Typography variant='body1'>
          Here you can access public boards and create new ones.
        </Typography>
      </Stack>
      <Box display='flex' justifyContent='center' alignItems='center' minHeight='40vh'>
        <Grid container spacing={2} columns={11} sx={{ flexDirection: { xs: "column", md: "row", lg: 'row', } }}>
          <Grid size={{ xs: 10, md: 5, lg: 5 }}>
            <Box padding={2}>
              <Typography variant='h4'>Public boards</Typography>
              {
                loading ?
                <LoadingSpinner /> :
                <List sx={{ width: '100%', maxWidth: 360, maxHeight: 360, overflow: 'auto', bgcolor: 'background.paper', }}>
                  {boards.map((board) => {
                    return (
                      <ListItem
                        key={board.id}
                        disablePadding
                      >
                        <ListItemButton  dense onClick={() => navigate("/" + board.id)}>
                          <ListItemText id={board.id} primary={board.name} />
                          <IconButton disableRipple edge="end" aria-label="goto" sx={{ padding: 0, }}>
                            <NavigateNextIcon />
                          </IconButton>
                        </ListItemButton>
                      </ListItem>
                    );
                  })}
                </List>
              }
            </Box>
            <Box padding={2}>
              <Typography variant='h4'>Private boards</Typography>
              <Typography variant="body1" gutterBottom>Private boards can only be accessed with direct links.</Typography>
            </Box>
          </Grid>
          <Divider orientation="vertical" flexItem sx={{ mr: "-1px" }} />
          <Grid size={{ xs: 10, md: 5, lg: 5, }}>
            <Box padding={2}>
              <Typography variant='h4'>Create new board</Typography>
              <FormGroup sx={{ display: 'inline' }}>
                    <FormControlLabel
                      control={<Switch checked={isPrivate} onChange={() => setIsPrivate(prev => !prev)} />}
                      label={
                        <Tooltip title="Private rooms won't be shown in the public listing and can be only accessed via direct links." >
                          <Typography display='inline'>
                            Private
                            <InfoIcon sx={{ fontSize: 15 }}/>
                          </Typography>
                        </Tooltip>
                      }/>
              </FormGroup>
              <form onSubmit={(e) => {
                e.preventDefault();
                handleCreateBoard();
                }}>
                <Box display='flex' alignItems='center'>
                  <TextField
                    required
                    label='Board name'
                    value={newBoardName}
                    onChange={(e) => setNewBoardName(e.target.value)} 
                    variant='outlined'
                    size='small'
                    fullWidth
                    error={boardNameError !== ''}
                    helperText={boardNameError}
                    slotProps={{
                      htmlInput: { maxLength: 100 }
                    }} />
                  <Button type='submit' disabled={submitIsClicked} variant='contained' color='primary' sx={{ marginLeft: '10px', minWidth: 'max-content', }}>
                    Create
                  </Button>
                </Box>
              </form>
            </Box>
          </Grid>
        </Grid>
      </Box>
    </>
  );
};
