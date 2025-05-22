/**
 * Hello World App with Banner
 */

import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { BannerAd } from './src/BannerAd';

function App(): React.JSX.Element {
  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" />
      <View style={styles.content}>
        <View style={styles.bannerContainer}>
          <Text style={styles.text}>Hello React</Text>
          
          <BannerAd width={300} height={600}  />
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  text: {
    fontSize: 30,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  bannerContainer: {
    alignItems: 'center',
    marginTop: 20,
  },
  bannerLabel: {
    fontSize: 16,
    marginBottom: 10,
  },
});

export default App;
