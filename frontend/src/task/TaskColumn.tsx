import { Box, Typography } from '@mui/material';
import { Droppable } from '@hello-pangea/dnd';
import { Task } from './task';
import { statusNames } from './statuses';
import { TaskCard } from './TaskCard';

interface TaskColumnProps {
  boardId: string;
  status: Task['status'];
  tasks: Task[];
};

export const TaskColumn = ({ boardId, status, tasks } : TaskColumnProps) => (
  <Box
    sx={{
      flex: 1,
      paddingTop: '8px',
      paddingBottom: '16px',
      background: 'white',
      '&:first-of-type': {
        paddingLeft: '5px',
        borderTopLeftRadius: 5,
      },
      '&:last-child': {
        paddingRight: '5px',
        borderTopRightRadius: 5,
      },
    }}
  >
    <Typography align='center' variant='subtitle1'>
      {statusNames[status]}
    </Typography>
    <Droppable droppableId={status}>
    {(droppableProvided, snapshot) => (
      <Box
        ref={droppableProvided.innerRef}
        {...droppableProvided.droppableProps}
        className={snapshot.isDraggingOver ? ' isDraggingOver' : ''}
        sx={{
          display: 'flex',
          flexDirection: 'column',
          borderRadius: 5,
          padding: '5px',
          '&.isDraggingOver': {
          bgcolor: '#dadadf',
          },
        }}
      >
        {tasks.map((task, position) => (
           <TaskCard boardId={boardId} key={task.id} task={task} position={position} />
         ))}
         {droppableProvided.placeholder}
      </Box>
      )}
    </Droppable>
  </Box>
);