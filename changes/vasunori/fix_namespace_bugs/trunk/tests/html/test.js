function log(message) {
  var entry = document.createElement('textarea');
  entry.className = 'log-entry';
  entry.value = message;
  document.body.appendChild(entry);
};
