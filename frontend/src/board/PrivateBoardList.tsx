import { Box, Typography } from "@mui/material";

export const PrivateBoardList = () => {
  return (
    <Box padding={2}>
      <Typography variant='h4'>Private boards</Typography>
      <Typography variant="body1" gutterBottom>Private boards can only be accessed with direct links.</Typography>
    </Box>
  );
};