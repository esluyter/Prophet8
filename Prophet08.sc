Prophet08 {
  var <globals, <voice;
  var <inDevice, <outDevice, defKey;

  *new { |deviceName, portName, channel = 0, makeDef = true, loadVoice = true|
    ^super.new.init(deviceName, portName, channel, makeDef, loadVoice);
  }

  init { |deviceName, portName, channel, makeDef, loadVoice|
    globals = P08Globals();
    voice = P08Voice();

    globals.channel = channel;

    [globals, voice].do(_.addDependant(this));

    if (deviceName.notNil) {
      this.makeDevice(deviceName, portName);
      if (makeDef) { this.makeDef };
      if (loadVoice) { this.loadVoice };
    };
  }

  makeDevice { |deviceName, portName|
    outDevice = MIDIOut.newByName(deviceName, portName);
    inDevice = MIDIIn.findPort(deviceName, portName);
    outDevice.latency = 0;
  }

  makeDef { |key = 'prophet'|
    // TODO
  }

  loadVoice {
    // TODO
  }

  channel {
    ^globals.channel.value
  }

  channel_ { |value|
    globals.channel = value;
    this.makeDef(defKey);
  }

  update { |object, param|
    this.changed(param);
  }

  layerA { ^voice.layerA }
  layerB { ^voice.layerB }
}
