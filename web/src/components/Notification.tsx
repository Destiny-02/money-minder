import { useEffect } from "react";
import { Transition, Notification } from "@mantine/core";
import { IconCheck } from "@tabler/icons-react";

interface Props {
  notification: string;
  showNotification: boolean;
  setShowNotification: (showNotification: boolean) => void;
}

const CustomNotification: React.FC<Props> = ({ notification, showNotification, setShowNotification }) => {
  useEffect(() => {
    if (showNotification) {
      const timer = setTimeout(() => {
        setShowNotification(false);
      }, 3000);

      return () => {
        clearTimeout(timer);
      };
    }
  }, [showNotification, setShowNotification]);

  return (
    <Transition mounted={showNotification} transition="fade" duration={500} timingFunction="ease" exitDuration={1000}>
      {(styles) => (
        <div style={{ width: "80%" }}>
          <Notification
            className="notification"
            icon={<IconCheck size="1.1rem" />}
            color="teal"
            title="Success"
            onClose={() => setShowNotification(false)}
            style={styles}
          >
            {notification}
          </Notification>
        </div>
      )}
    </Transition>
  );
};

export default CustomNotification;
