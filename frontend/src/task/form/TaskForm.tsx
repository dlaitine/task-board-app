import { useEffect, useState } from 'react';
import {
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';

interface TaskFormProps {
  isOpen: boolean;
  onClose: () => void;
  dialogTitle: string;
  onSubmit: (title: string, description: string) => void;
  defaultTitle?: string;
  defaultDescription?: string;
}

export const TaskForm = ({
  isOpen,
  onClose,
  dialogTitle,
  onSubmit,
  defaultTitle = '',
  defaultDescription = '',
}: TaskFormProps) => {
  // Inputs
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');

  const [submitIsClicked, setSubmitIsClicked] = useState<boolean>(false);

  // Errors
  const [titleError, setTitleError] = useState<string>('');

  useEffect(() => {
    // Set default values here, so that state changes will be updated correctly
    setTitle(defaultTitle);
    setDescription(defaultDescription);
  }, [defaultTitle, defaultDescription]);

  const handleSubmit = () => {
    if (title === '') {
      setTitleError('Required');
      setSubmitIsClicked(false);
      return;
    }

    onSubmit(title, description);

    setTitle(defaultTitle);
    setDescription(defaultDescription);
    setTitleError('');
    setSubmitIsClicked(false);

    onClose();
  };

  return (
    <>
      <Dialog open={isOpen} onClose={onClose}>
        <DialogTitle variant="h5">{dialogTitle}</DialogTitle>
        <DialogContent>
          <TextField
            label="Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            margin="normal"
            fullWidth
            required
            error={titleError !== ''}
            helperText={titleError}
            slotProps={{
              htmlInput: { maxLength: 255 },
            }}
          />
          <TextField
            label="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            margin="normal"
            fullWidth
            multiline
            minRows={4}
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={handleSubmit}
            disabled={submitIsClicked}
            color="primary"
            variant="contained"
          >
            Save
          </Button>
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};
