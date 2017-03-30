import React, {Component, PropTypes} from 'react';
import {View, Text, Image, TouchableHighlight} from 'react-native';

export default class Station extends Component {
  static propTypes = {
    name: PropTypes.string.isRequired,
    show: PropTypes.shape({
      title: PropTypes.string,
      description: PropTypes.string,
      location: PropTypes.string,
      url: PropTypes.string,
      imageUrl: PropTypes.string
    }),
    onPress: PropTypes.func,
    isActive: PropTypes.bool,
    playStatus: PropTypes.string
  }

  render() {
    const backgroundColor = this.props.isActive && this.props.playStatus !== 'stopped' ? '#fff' : '#000'
    const textColor = this.props.isActive && this.props.playStatus !== 'stopped' ? '#000' : '#fff'
    const borderColor = textColor

    return (
      <TouchableHighlight style={{flex: 1}} onPress={this.props.onPress}>
        {this.withImageWrapper(this.props.show.imageUrl,
          (<View style={{flexDirection: 'column'}}>
            <View style={{
              backgroundColor: backgroundColor,
              borderColor: borderColor,
              borderWidth: 2,
            }}>
              <View style={{borderBottomColor: borderColor, borderBottomWidth: 1, padding: 6}}>
                <Text style={{color: textColor, fontSize: 14, fontFamily: 'monospace', fontWeight: 'bold'}}>
                  {this.props.show.title}
                  {this.props.show.location && ` (${this.props.show.location})`}
                </Text>
              </View>
              {this.props.show.description &&
                <View>
                  <Text style={{color: textColor, fontFamily: 'monospace', fontSize: 12, padding: 6}}>
                    {this.props.show.description}
                  </Text>
                </View>}
              {this.props.isActive && this.props.playStatus === 'buffering' &&
                <View style={{borderTopColor: borderColor, borderTopWidth: 1, padding: 6}}>
                  <Text style={{color: textColor, fontFamily: 'monospace', fontSize: 14, fontWeight: 'bold'}}>
                    Loading...
                  </Text>
                </View>}
            </View>
          </View>))}
      </TouchableHighlight>
    );
  }

  friendlyStatus(status) {
    if (status === 'buffering') {
      return 'Loading...';
    }

    if (status === 'playing') {
      return 'Now Playing';
    }

    if (status === 'stopped') {
      return 'Stopped';
    }
  }

  withImageWrapper(imageUrl, body) {
    const style = {flex: 1, justifyContent: 'flex-end', padding: 15};
    if (imageUrl) {
      return (<Image source={{uri: imageUrl}} style={{...style, resizeMode: 'cover'}}>{body}</Image>);
    }
    else {
      return (<View style={{...style, backgroundColor: '#000'}}>{body}</View>);
    }
  }
}
