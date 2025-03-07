import { Box, Card, CardContent, CardActions, Typography } from '@mui/material';
import { Draggable } from '@hello-pangea/dnd';
import { Task } from './task';
import { UpdateTaskForm } from './form/UpdateTaskForm';

interface TaskCardProps {
  task: Task;
  index: number;
}

export const TaskCard = ({ task, index }: TaskCardProps) => {
  const { title, description } = task;
  return (
    <Draggable draggableId={String(task.id)} index={index}>
      {(provided, snapshot) => (
        <Box
          sx={{ marginBottom: 1 }}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          ref={provided.innerRef}
        >
          <Card
            style={{
              opacity: snapshot.isDragging ? 0.9 : 1,
              transform: snapshot.isDragging ? 'rotate(-2deg)' : '',
              background: '#FFF9C4',
            }}
            elevation={snapshot.isDragging ? 3 : 1}
          >
            <CardContent>
              <Typography variant="h5" component="div">
                {title}
              </Typography>
              <Typography variant="body2">
                {description.substring(0, Math.min(description.length, 100))}
                {description.length > 100 ? '...' : ''}
              </Typography>
            </CardContent>
            <CardActions>
              <UpdateTaskForm task={task} />
            </CardActions>
          </Card>
        </Box>
      )}
    </Draggable>
  );
};
