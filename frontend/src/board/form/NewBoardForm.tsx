import { useContext, useState } from 'react';
import { NewBoard } from '../board';
import {
  Box,
  Typography,
  FormGroup,
  FormControlLabel,
  Switch,
  Tooltip,
  TextField,
  Button,
} from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import { baseUrl } from '../../common/constants';
import { NotificationContext } from '../../context/NotificationContext';
import { useNavigate } from 'react-router-dom';

export const NewBoardForm = () => {
  const navigate = useNavigate();

  const [newBoardName, setNewBoardName] = useState('');
  const [boardNameError, setBoardNameError] = useState<string>('');

  const [isPrivate, setIsPrivate] = useState(false);

  const [submitIsClicked, setSubmitIsClicked] = useState<boolean>(false);

  const { setMessage, setSeverity } = useContext(NotificationContext);

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

    fetch(`${baseUrl}/api/v1/boards`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(newBoard),
    })
      .then((response) => {
        if (!response.ok) {
          throw Error('Board creation failed');
        }
        return response;
      })
      .then((response) => response.json())
      .then((data) => {
        setSeverity('success');
        setMessage("New board '" + newBoardName + "' created successfully");
        navigate(`/${data.id}`);
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
    <Box padding={2}>
      <Typography variant="h4">Create new board</Typography>
      <FormGroup sx={{ display: 'inline' }}>
        <FormControlLabel
          control={
            <Switch
              checked={isPrivate}
              onChange={() => setIsPrivate((prev) => !prev)}
            />
          }
          label={
            <Tooltip title="Private rooms won't be shown in the public listing and can be only accessed via direct links.">
              <Typography display="inline">
                Private
                <InfoIcon sx={{ fontSize: 15 }} />
              </Typography>
            </Tooltip>
          }
        />
      </FormGroup>
      <form
        onSubmit={(e) => {
          e.preventDefault();
          handleCreateBoard();
        }}
      >
        <Box display="flex" alignItems="center">
          <TextField
            required
            label="Board name"
            value={newBoardName}
            onChange={(e) => setNewBoardName(e.target.value)}
            variant="outlined"
            size="small"
            fullWidth
            error={boardNameError !== ''}
            helperText={boardNameError}
            slotProps={{
              htmlInput: { maxLength: 100 },
            }}
          />
          <Button
            type="submit"
            disabled={submitIsClicked}
            variant="contained"
            color="primary"
            sx={{ marginLeft: '10px', minWidth: 'max-content' }}
          >
            Create
          </Button>
        </Box>
      </form>
    </Box>
  );
};
