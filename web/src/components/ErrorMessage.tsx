import { Alert } from "@mantine/core";
import { IconAlertCircle } from "@tabler/icons-react";

interface Props {
  message: string;
}

const ErrorMessage: React.FC<Props> = ({ message }) => {
  return (
    <Alert icon={<IconAlertCircle size="1.1rem" />} title="Error" color="red">
      {message}
    </Alert>
  );
};

export default ErrorMessage;
