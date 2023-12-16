import { CopyButton, ActionIcon, Tooltip, Space } from "@mantine/core";
import { IconCopy, IconCheck } from "@tabler/icons-react";

interface Props {
  copyText: string;
}

const CustomCopyButton: React.FC<Props> = ({ copyText }) => {
  return (
    <>
      <Space w="xs" />
      <CopyButton value={copyText} timeout={2000}>
        {({ copied, copy }) => (
          <Tooltip label={copied ? "Copied" : "Copy"} withArrow position="right">
            <ActionIcon color={copied ? "teal" : "gray"} onClick={copy}>
              {copied ? <IconCheck size="1.2rem" /> : <IconCopy size="1.2rem" />}
            </ActionIcon>
          </Tooltip>
        )}
      </CopyButton>
    </>
  );
};

export default CustomCopyButton;
