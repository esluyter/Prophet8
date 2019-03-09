P08Voice : P08ParamBank {
  var <layerA, <layerB;
  var <splitPoint, <keyboardMode;
  var name;

  init {
    layerA = P08Layer();
    layerB = P08Layer(200);
    splitPoint = P08Param(118, value: 60); // 0-127, 60=C3
    keyboardMode = P08Param(119, 0, 2); // 0 normal, 1 stack, 2 split
    name = String.newFrom($ .dup(16)); // TODO make this an editable param
  }
}
