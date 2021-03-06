P08MidiController {
  var <inDevice, <outDevice, midiFunc;
  var model;

  *new { |model, deviceName, portName, loadVoice|
    ^super.new.init(model, deviceName, portName, loadVoice);
  }

  init { |argModel, deviceName, portName, loadVoice|
    model = argModel;
    if (MIDIClient.initialized.not) {
      MIDIClient.init;
      MIDIIn.connectAll;
    };

    try {
      outDevice = MIDIOut.newByName(deviceName, portName);
      inDevice = MIDIIn.findPort(deviceName, portName);
      outDevice.latency = 0;
    } {
      ^nil;
    };

    this.makeDef;
    model.addDependant(this);
  }

  makeDef {
    var nrpn = 0!4;

    if (midiFunc.notNil) {
      midiFunc.free;
    };

    midiFunc = MIDIFunc.cc({ |val, num, channel|
      if ((channel == (model.globals.channel.value - 1)) || (model.globals.channel.value == 0)) {
        if (num == 0x63) {
          nrpn[0] = val;
        };
        if (num == 0x62) {
          nrpn[1] = val;
        };
        if (num == 0x06) {
          nrpn[2] = val;
        };
        if (num == 0x26) {
          nrpn[3] = val;
          this.prReceiveNRPN(nrpn);
        };
      };
    }, srcID: inDevice.uid);
  }

  prReceiveNRPN { |nrpn|
    var paramNum, paramVal;
    paramNum = nrpn[0] * 128 + nrpn[1];
    paramVal = nrpn[2] * 128 + nrpn[3];
    if (model.ids[paramNum].notNil) {
      model.ids[paramNum].midiValue = paramVal;
    } {
      "Whoa now, got some NRPN with no idea what to do with it: ".post;
      [paramNum, paramVal].postln;
    }
  }

  update { |object, param|
    this.prSendNRPN(param);
  }

  prSendNRPN { |param|
    var channel = if (model.globals.channel.value == 0) { 0 } { model.globals.channel.value - 1 };
    var numMSB = (param.id / 128).floor;
    var numLSB = param.id % 128;
    var valMSB = (param.midiValue / 128).floor;
    var valLSB = param.midiValue % 128;
    outDevice.control(channel, 0x63, numMSB);
    outDevice.control(channel, 0x62, numLSB);
    outDevice.control(channel, 0x06, valMSB);
    outDevice.control(channel, 0x26, valLSB);
  }

  loadVoice {
    // TODO
  }
}
