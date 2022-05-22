P08MidiController {
  var <inDevice, <outDevice, midiFunc, <>midiOn = true;
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
      ^this;
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
    if (midiOn) {
      this.prSendNRPN(param);
    };
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

  prRequestProgramEditBuffer {
    outDevice.sysex(Int8Array[0xF0, 0x01, 0x23, 0x06, 0xF7]);
  }

  prReceiveSysex { |data|
    if (data[0..3] == Int8Array[0xF0, 0x01, 0x23, 0x03]) {
      // Program Edit Buffer Data Dump
      data = this.prUnpackProgramData(data[4..(data.size - 2)]);
      this.prReceiveProgramEditBuffer(data);
    };
  }

  prUnpackProgramData { |data|
    if (data.size != 439) { "Wrong data size".error };
    data = data.collect(_.asBinaryDigits).clump(8).collect { |packet|
      var msBits = packet[0];
      if (msBits[0] != 0) { "Received bad sysex".error };
      packet = packet[1..].collect { |bits, i|
        if (bits[0] != 0) { "Received bad sysex".error };
        bits[0] = msBits[7 - i];
        bits.convertDigits(2);
      };
    };
    ^data.flat;
  }

  prReceiveProgramEditBuffer { |data|
    midiOn = false;
    data.do { |value, num|
      var param = model[num];
      //[param, num, value].postln;
      if (param.notNil) {
        //[param, param.midiValue, value].postln;
        param.midiValue = value;
      };
    };
    midiOn = true;
  }
}
