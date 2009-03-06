var service = null;
var privateGadgetNames = null;
var nameOfGadgetBeingEdited = '';
var entryOfGadgetBeingEdited = null;
var privateGadgetSpecFeedUrl = null;

var SERVICE_NAME = 'esp';
var APP_NAME = 'private-gadget-editor';

var SPINNER = '<img src="http://google-feedserver.googlecode.com/svn/trunk/resources/gadgets/private-gadget-editor/spinner.gif">';
var DEFAULT_SPINNER_ID = 'spinner';

function $(id) {
  return document.getElementById(id);
};

function startSpinner(id) {
  $(id ? id : DEFAULT_SPINNER_ID).innerHTML = SPINNER;
};

function stopSpinner(id) {
  $(id ? id : DEFAULT_SPINNER_ID).innerHTML = '';
};

function initEditor() {
  if (privateGadgetSpecFeedUrl) {
    initGadgetNameList();
  }
};

function noCache(url) {
  return url + '?nocache=' + (new Date().getTime());
};

function initGadgetNameList() {
  service = new google.gdata.client.FeedServerService(SERVICE_NAME, APP_NAME);
  service.setGadgetsAuthentication('SIGNED');
  service.getFeed(noCache(privateGadgetSpecFeedUrl), function(response) {
    stopSpinner();
    var entries = response ? response.feed.entry : [];
    showPrivateGadgetNames(setPrivateGadgetNames(entries));
  }, function(response) {
    stopSpinner()
    showMessage('Error: failed to load private gadget specs');
  });
  startSpinner();
};

function setPrivateGadgetNames(entries) {
  privateGadgetNames = [];
  for (var i = 0; i < entries.length; i++) {
    var name = entries[i].id.$t;
    name = name.substring(name.lastIndexOf('/') + 1);
    privateGadgetNames.push(name);
  }
  return privateGadgetNames;
};

function showPrivateGadgetNames() {
  var html = ['<select id="gadget-select" onchange="editSelectedGadget()">'];
  html.push('<option value="">Select to open</option>');
  for (var i = 0; i < privateGadgetNames.length; i++) {
    var privateGadgetName = privateGadgetNames[i];
    html.push('<option ', privateGadgetName == nameOfGadgetBeingEdited ? 'selected' : '',
        '>', privateGadgetName, '</option>');
  }
  document.getElementById('gadget-list').innerHTML = html.join('');
};

function getSelectedGadgetName() {
  return document.getElementById('gadget-select').value;
};

function editSelectedGadget() {
  var name = getSelectedGadgetName();
  if (name) {
    service.getEntry(noCache(privateGadgetSpecFeedUrl + '/' + name), function(response) {
      stopSpinner();
      nameOfGadgetBeingEdited = name;
      entryOfGadgetBeingEdited = response.entry;
      editor.setCode(response.entry.content.entity.specContent);
      editor.editor.syntaxHighlight('init');
    }, function(response) {
      stopSpinner();
      showMessage('Error: failed to open gadget spec "' + name + '"');
    });
    startSpinner();
  } else {
    newGadget();
  }
};

function deleteSelectedGadget() {
  var name = getSelectedGadgetName();
  if (name) {
    service.deleteEntry(privateGadgetSpecFeedUrl + '/' + name, function(response) {
      stopSpinner();
      initEditor();
      newGadget();
    }, function(response) {
      stopSpinner();
      showMessage('Error: failed to delete gadget spec "' + name + '"');
    });
    startSpinner();
  } else {
    alert('Please select a gadget to delete.');
  }
};

function newGadget() {
  nameOfGadgetBeingEdited = '';
  editor.setCode(getGadgetSpecTemplate());
  editor.editor.syntaxHighlight('init');
  showPrivateGadgetNames();
  entryOfGadgetBeingEdited = {xmlns: 'http://www.w3.org/2005/Atom', content: {
      type: 'application/xml', entity: {name: '', specContent: ''}}};
};

function saveGadget(changeName) {
  entryOfGadgetBeingEdited.content.entity.specContent = editor.getCode();
  if (nameOfGadgetBeingEdited && !changeName) {
    service.updateEntry(privateGadgetSpecFeedUrl + '/' + nameOfGadgetBeingEdited,
        entryOfGadgetBeingEdited, function(response) {
      stopSpinner();
    }, function(response) {
      stopSpinner();
      showMessage('Error: failed to update gadget spec "' + nameOfGadgetBeingEdited + '"');
    });
    startSpinner();
  } else {
    if (changeName) {
      nameOfGadgetBeingEdited = prompt('Please enter new name of gadget (e.g. hello.xml)');
    } else {
      nameOfGadgetBeingEdited = prompt('Please enter name of new gadget (e.g. hello.xml)');
    }
    if (nameOfGadgetBeingEdited) {
      entryOfGadgetBeingEdited.content.entity.name = nameOfGadgetBeingEdited;
      service.insertEntry(privateGadgetSpecFeedUrl,
          entryOfGadgetBeingEdited, function(response) {
        stopSpinner();
        privateGadgetNames.push(nameOfGadgetBeingEdited);
        showPrivateGadgetNames();
      }, function(response) {
        stopSpinner();
        showMessage('Error: failed to add gadget spec "' + nameOfGadgetBeingEdited + '"');
      });
      startSpinner();
    }
  }
};

function openGadgetByUrl() {
  var gadgetSpecUrl = prompt('Please enter public URL of gadget spec');
  if (gadgetSpecUrl) {
    var params = {};
    params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.TEXT;
    gadgets.io.makeRequest(gadgetSpecUrl, function(response) {
      if (response.rc > 299) {
        showMessage('Error: ' + response.errors);
      } else {
        nameOfGadgetBeingEdited = null;
        entryOfGadgetBeingEdited = {xmlns: 'http://www.w3.org/2005/Atom', content: {
          type: 'application/xml', entity: {name: '', specContent: response.text}}};
        editor.setCode(response.text);
        editor.editor.syntaxHighlight('init');
      }
    }, params);
  }
};

function getGadgetSpecTemplate() {
  var textArea = document.getElementById('gadget-spec-template');
  return textArea.value || textArea.defaultValue;
};

function showMessage(message) {
  var miniMessage = new gadgets.MiniMessage(null, document.getElementById('message-box'));
  miniMessage.createDismissibleMessage(message);
};

//// google.load('gdata', '1.x', {packages: ['core']});
// google.load('gdata', '1.x', {packages: ['core'], 'other_params': 'debug=1'});
// google.setOnLoadCallback(initEditor);

function initCodePress() {
  editor.style.height = '400px';
  CodePressWrapper.init();
};

function detectDomainName() {
  var params = location.href.split('&');
  for (var i = 0; i < params.length; i++) {
    if (params[i].indexOf('parent=') == 0) {
      var p = params[i].split('=');
      var parent = decodeURIComponent(p[1]);
      var r = /google.com\/a\/([^\/]*)\//.exec(parent);
      var domainName = r ? r[1] : null;
      return domainName;
    }
  }

  return null;
};

function getDomainName() {
  return new gadgets.Prefs().getString("domainName") || detectDomainName();
};

function initGadget() {
  var domainName = getDomainName();
  if (domainName) {
    privateGadgetSpecFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/a/' +
        domainName + '/g/PrivateGadgetSpec';
  } else {
    showMessage('Error: domain name missing');
  }

  gadgets.window.adjustHeight();
};

function init() {
  initCodePress();
  initGadget();
  initEditor();
};

gadgets.util.registerOnLoadHandler(init);
