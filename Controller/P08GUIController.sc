P08GUIController {
  var <model, <window, <view;

  *new { |model, bounds = (Rect(0, 0, 1080, 530))|
    ^super.new.initModel(model).makeWindow(bounds).refreshView;
  }

  initModel { |argModel|
    model = argModel;
    model.addDependant(this);
  }

  makeWindow { |bounds|
    if (window.notNil) { window.close };
    window = Window("Prophet '08", bounds.resizeBy(60, 0)).front
      .background_(Color.hsv(0.05, 0.9, 0.5));
    view = P08View(window, bounds.moveBy(30, 0));
    view.addDependant(this);
  }

  refreshView {
    model.ids.do { |param|
      this.update(model, param);
    };
  }

  update { |object, param|
    if (object.class == Prophet08) {
      defer { this.prUpdateView(param) };
    };
    if (object.class == P08View) {
      //[param.id, param.value].postln;
      if (param.id.isInteger) {
        model.ids[param.id].midiValue = param.value;
      };
    };
  }

  prUpdateView { |param|
    var guiParam = view.ids[param.id];
    if (guiParam.notNil) {
      if (guiParam.value != param.midiValue) {
        guiParam.value = param.midiValue;
      };
    };
  }
}
