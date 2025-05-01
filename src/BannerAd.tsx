import { requireNativeComponent, ViewProps } from 'react-native';

interface BannerAdProps extends ViewProps {
  width: number;
  height: number;
}

export const BannerAd = requireNativeComponent<BannerAdProps>('BannerAd'); 