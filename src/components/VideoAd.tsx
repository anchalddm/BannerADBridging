import React from 'react';
import { requireNativeComponent, ViewStyle, StyleSheet } from 'react-native';

interface VideoAdProps {
  style?: ViewStyle;
  width?: number;
  height?: number;
  onVideoAdLoaded?: () => void;
  onVideoAdFailedToLoad?: (error: string) => void;
}

const VideoAdView = requireNativeComponent<VideoAdProps>('VideoAd');

export const VideoAd: React.FC<VideoAdProps> = ({
  style,
  width,
  height,
  onVideoAdLoaded,
  onVideoAdFailedToLoad,
}) => {
  return (
    <VideoAdView
      style={StyleSheet.flatten([{ width, height }, style])}
      width={width}
      height={height}
      onVideoAdLoaded={onVideoAdLoaded}
      onVideoAdFailedToLoad={onVideoAdFailedToLoad}
    />
  );
};

export default VideoAd; 