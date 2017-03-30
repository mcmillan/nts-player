import React, {Component} from 'react'
import {View, StatusBar} from 'react-native'
import StationList from './components/StationList'

export default class Main extends Component {
  render () {
    return (
      <View style={{flex: 1, paddingTop: 20, backgroundColor: '#000'}}>
        <StatusBar backgroundColor='black' barStyle='light-content' />
        <StationList />
      </View>
    )
  }
}
