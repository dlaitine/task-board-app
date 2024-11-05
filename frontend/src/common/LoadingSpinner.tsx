import { CircularProgress } from '@mui/material';
import { makeStyles } from '@mui/styles';

const useStyles = makeStyles({
  spinner: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    paddingTop: '10%',
  },
});

const LoadingSpinner = () => {
  const classes = useStyles(); // Corrected usage of makeStyles

  return (
    <div className={classes.spinner}>
      <CircularProgress />
    </div>
  );
};

export default LoadingSpinner;
