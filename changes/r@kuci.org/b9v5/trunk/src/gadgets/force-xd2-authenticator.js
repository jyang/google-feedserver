function ForceXd2Authenticator(service) {
  google.gdata.client.Authenticator.call(this, service);
};

ForceXd2Authenticator.prototype = new google.gdata.client.Authenticator();

ForceXd2Authenticator.prototype.isAuthenticationRequired = function() {
  return true;
};

ForceXd2Authenticator.prototype.isAuthenticated = function() {
  return true;
};

ForceXd2Authenticator.prototype.setAuthHeaders = function() {
};
