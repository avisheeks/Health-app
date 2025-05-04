import React from 'react';
import { 
  Box, 
  Drawer, 
  List, 
  ListItem, 
  ListItemButton, 
  ListItemIcon, 
  ListItemText,
  Divider
} from '@mui/material';
import { 
  DashboardOutlined, 
  CalendarMonthOutlined, 
  FavoriteBorderOutlined, 
  MessageOutlined,
  PermIdentityOutlined,
  DescriptionOutlined,
  MenuBookOutlined,
  VideocamOutlined,
  WatchOutlined
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface SidebarItem {
  title: string;
  icon: React.ReactNode;
  path: string;
  roles?: string[];
}

interface SidebarProps {
  open: boolean;
  onClose: () => void;
  variant: "permanent" | "persistent" | "temporary";
}

const Sidebar: React.FC<SidebarProps> = ({ open, onClose, variant }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();
  
  const menuItems: SidebarItem[] = [
    {
      title: 'Dashboard',
      icon: <DashboardOutlined />,
      path: '/dashboard',
    },
    {
      title: 'Appointments',
      icon: <CalendarMonthOutlined />,
      path: '/appointments',
    },
    {
      title: 'Health Metrics',
      icon: <FavoriteBorderOutlined />,
      path: '/health-metrics',
      roles: ['PATIENT'],
    },
    {
      title: 'Wearable Data',
      icon: <WatchOutlined />,
      path: '/wearable-data',
      roles: ['PATIENT'],
    },
    {
      title: 'Messages',
      icon: <MessageOutlined />,
      path: '/messages',
    },
    {
      title: 'Profile',
      icon: <PermIdentityOutlined />,
      path: '/profile',
    },
    {
      title: 'Reports',
      icon: <DescriptionOutlined />,
      path: '/reports',
    },
    {
      title: 'Resources',
      icon: <MenuBookOutlined />,
      path: '/resources',
    },
    {
      title: 'Telemedicine',
      icon: <VideocamOutlined />,
      path: '/telemedicine',
    },
  ];

  const handleNavigate = (path: string) => {
    navigate(path);
    if (variant === "temporary") {
      onClose();
    }
  };
  
  // Filter menu items based on user roles
  const filteredMenuItems = menuItems.filter(item => {
    if (!item.roles) return true;
    if (!user || !user.user_metadata || !user.user_metadata.role) return false;
    return item.roles.includes(user.user_metadata.role);
  });

  return (
    <Drawer
      variant={variant}
      open={open}
      onClose={onClose}
      sx={{
        width: 240,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: 240,
          boxSizing: 'border-box',
        },
      }}
    >
      <Box sx={{ p: 2 }}>
        <Box sx={{ mb: 2, fontWeight: 'bold', fontSize: '1.2rem' }}>
          Hospital System
        </Box>
        <Divider />
        <List>
          {filteredMenuItems.map((item) => (
            <ListItem key={item.path} disablePadding>
              <ListItemButton
                selected={location.pathname === item.path}
                onClick={() => handleNavigate(item.path)}
                sx={{
                  borderRadius: 1,
                  my: 0.5,
                  '&.Mui-selected': {
                    backgroundColor: 'primary.light',
                    '&:hover': {
                      backgroundColor: 'primary.light',
                    },
                  },
                }}
              >
                <ListItemIcon sx={{ minWidth: 40 }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.title} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Box>
    </Drawer>
  );
};

export default Sidebar; 