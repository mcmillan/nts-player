import React, {Component} from 'react';
import {View, ToolbarAndroid, StatusBar} from 'react-native'
import StationList from './components/StationList';

export default class Main extends Component {
  render() {
    return (
      <View style={{flex: 1}}>
        <StatusBar backgroundColor="black" />
        <StationList />
      </View>
    );
  }
}
