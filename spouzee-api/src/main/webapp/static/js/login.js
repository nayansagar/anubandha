var globalUserId;
var app = angular.module('spzApp', ['ngRoute', 'ngAnimate', 'ngTouch']);

app.run(function($http, $rootScope) {
  $http.defaults.headers.common.sid = $rootScope.sessionId;
});

app.factory('httpRequestInterceptor', function ($rootScope) {
  return {
    request: function (config) {

      config.headers['sid'] = $rootScope.sessionId;

      return config;
    }
  };
});

app.config(function ($httpProvider) {
  $httpProvider.interceptors.push('httpRequestInterceptor');
});

app.directive('httpSrc', [
        '$http', function ($http, $rootScope) {
            var directive = {
                link: link,
                restrict: 'A'
            };
            return directive;

            function link(scope, element, attrs) {
                var requestConfig = {
                    method: 'Get',
                    url: attrs.httpSrc,
                    responseType: 'arraybuffer',
                    cache: 'true'
                };

                $http(requestConfig)
                    .success(function(data) {
                        var arr = new Uint8Array(data);

                        var raw = '';
                        var i, j, subArray, chunk = 5000;
                        for (i = 0, j = arr.length; i < j; i += chunk) {
                            subArray = arr.subarray(i, i + chunk);
                            raw += String.fromCharCode.apply(null, subArray);
                        }

                        var b64 = btoa(raw);

                        attrs.$set('src', "data:image/jpeg;base64," + b64);
                    });
            }

        }
    ]);

app.config(function($routeProvider, $httpProvider){
  $routeProvider
  .when("/",
    {
      templateUrl: "/spouzee-api/home.html"
    }
  ).when("/how",
    {
      templateUrl: "/spouzee-api/howItWorks.html"
    }
  ).when("/value",
    {
      templateUrl: "/spouzee-api/whySpouzee.html"
    }
  ).when("/faqs",
    {
      templateUrl: "/spouzee-api/FAQs.html"
    }
  ).when("/terms",
    {
      templateUrl: "/spouzee-api/terms.html"
    }
  ).when("/details",
    {
      templateUrl: "/spouzee-api/details.html",
      controller: "detailsCtrl"
    }
  ).when("/questions",
    {
      templateUrl: "/spouzee-api/questions.html",
      controller: "questionsCtrl"
    }
  ).when("/report",
    {
      templateUrl: "/spouzee-api/report.html",
      controller: "reportCtrl"
    }
  ).when("/reportScr",
    {
      templateUrl: "/spouzee-api/reportScrolling.html",
      controller: "scrollReportCtrl"
    }
  ).when("/invite",
    {
      templateUrl: "/spouzee-api/invite.html",
      controller: "inviteCtrl"
    }
  ).when("/match",
    {
      templateUrl: "/spouzee-api/browseMatch.html",
      controller: "browseCtrl"
    }
  ).when("/expectI",
    {
      templateUrl: "/spouzee-api/expectI.html",
      controller: "expectICtrl"
    }
  ).when("/expect",
    {
      templateUrl: "/spouzee-api/expectation.html",
      controller: "expectCtrl"
    }
  ).when("/imgs",
    {
      templateUrl: "/spouzee-api/imageUpload.html",
      controller: "uploadCtrl"
    }
  ).otherwise(
    {
      redirectTo: '/'
    }
  );
});

/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('detailsCtrl', function($scope, $http, $rootScope, $window) {
            console.log("rootScope userId : "+$rootScope.userId);

            /*var validateDOB = function(dob){
                console.log("dob : "+dob);
                if (dob != null && dob != "" && dob.match(/^(?:(0[1-9]|[12][0-9]|3[01])[\/](0[1-9]|1[012])[\/](19|20)[0-9]{2})$/)){
                    return true;
                  }else{
                    return false;
                  }
            }*/

            var fetchProfile = function(){
                var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/profile?ru='+$rootScope.userId);
                res.success(function(data, status, headers, config) {
                    $scope.name = (data.name != null && data.name != "") ? data.name : "Name*";
                    $scope.dob = (data.dateOfBirth != null && data.dateOfBirth != "") ? data.dateOfBirth : "Date of Birth (dd/mm/yyyy)*";
                    $scope.religion = (data.religion != null && data.religion != "") ? data.religion : "Religion";
                    $scope.caste = (data.caste != null && data.caste != "") ? data.caste : "Caste";
                    $scope.subcaste = (data.subcaste != null && data.subcaste != "") ? data.subcaste : "Subcaste";
                    $scope.language = (data.language != null && data.language != "") ? data.language : "Language";
                    $scope.employment = (data.employment != null && data.employment != "") ? data.employment : "Employment";
                    $scope.qualification = (data.qualification != null && data.qualification != "") ? data.qualification : "Qualification";
                    $scope.role = (data.role != null && data.role != "") ? data.role : "Role*";
                    $scope.maritalStatus = (data.maritalStatus != null && data.maritalStatus != "") ? data.maritalStatus : "Marital Status*";
                });
                res.error(function(data, status, headers, config) {
                    alert( "failure message: " + JSON.stringify({data: data}));
                });
            }
            fetchProfile();

            var validateInputs = function(){
                if ($scope.dob == null || $scope.dob == "" || !$scope.dob.match(/^(?:(0[1-9]|[12][0-9]|3[01])[\/](0[1-9]|1[012])[\/](19|20)[0-9]{2})$/)){
                    alert("Please enter valid DOB in dd/mm/yyyy format");
                    return false;
                }

                if($scope.name == null || $scope.name == ""){
                    alert("Please enter your name");
                    return false;
                }

                if($scope.role == null || $scope.role == ""){
                    alert("Please select your role");
                    return false;
                }

                if($scope.maritalStatus == null || $scope.maritalStatus == ""){
                    alert("Please select your marital status");
                    return false;
                }
                return true;
            }

            $scope.submitDetails = function(){
                console.log("DOB1 : "+$scope.dob);
                if(!validateInputs()){
                    return;
                }

                var dataObj = {
                				name : $scope.name,
                				dateOfBirth : $scope.dob,
                				religion : $scope.religion,
                				caste : $scope.caste,
                				subcaste : $scope.subcaste,
                				language : $scope.language,
                				employment : $scope.employment,
                				qualification : $scope.qualification,
                				role : $scope.role,
                				maritalStatus : $scope.maritalStatus
                		    };

                var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'?ru='+$rootScope.userId, dataObj);
                res.success(function(data, status, headers, config) {
                    $rootScope.profileCreated=true;
                	$window.location.href = '#expectI';
                });
                res.error(function(data, status, headers, config) {
                	alert( "failure message: " + JSON.stringify({data: data}));
                });

                console.log("Name : "+$scope.name);
            }

            $scope.uploadFiles = function(){
                    console.log("In uploadFiles");
                    var form = document.getElementById('photo-upload');
                    var formData = new FormData(form);
                    var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/images?ru='+$rootScope.userId, formData, {
                       transformRequest: angular.identity,
                       headers: {'Content-Type': undefined}
                    });

                    res.success(function(data, status, headers, config) {
                       loadPictures();
                       alert("Uploaded!");
                    });
                    res.error(function(data, status, headers, config) {
                       alert("Upload failed. Please try again...");
                    });
                }

                $scope.getContentType = function(fileName){
                    if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")){
                        return "image/jpg";
                    }else if(fileName.endsWith(".png")){
                        return "image/png";
                    }else{
                        return "undefined";
                    }
                }

            /*Image gallery*/

                var loadPictures = function(){
                            var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/images?ru='+$rootScope.userId);
                                res.success(function(data, status, headers, config) {
                                    $rootScope.photos = data;
                                });
                                res.error(function(data, status, headers, config) {
                                    alert( "failure message: " + JSON.stringify({data: data}));
                                });

                    }
                    loadPictures();

                // initial image index
                    $scope._Index = 0;

                    // if a current image is the same as requested image
                    $scope.isGalleryActive = function (index) {
                        return $scope._Index === index;
                    };

                    // show prev image
                    $scope.showPrev = function () {
                        $scope._Index = ($scope._Index > 0) ? --$scope._Index : $scope.photos.length - 1;
                    };

                    // show next image
                    $scope.showNext = function () {
                        $scope._Index = ($scope._Index < $scope.photos.length - 1) ? ++$scope._Index : 0;
                    };

                    // show a certain image
                    $scope.showPhoto = function (index) {
                        $scope._Index = index;
                    };
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('questionsCtrl', function($scope, $http, $rootScope, $window, $route) {
    var lastQuestionId = -1;
    var question;
    var responseType=2;
    var selectedOptions;
    var selectedOptionsArray = {};
    var responseChanged=false;

    var contains = function(a, obj) {
            var i = a.length;
            while (i--) {
               if (a[i] == obj) {
                   return i;
               }
            }
            return -1;
        }

    var loadQuestion = function(){
        console.log("QCTRL :: $rootScope.userId : "+$rootScope.userId);
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/quesNext?lastQuestionId='+lastQuestionId+'&pageSize=1&ru='+$rootScope.userId);
            res.success(function(data, status, headers, config) {
                if(status === 202){
                    $scope.question="That's all for now! Please come back later to tell us more about you..."
                    $window.location.href = '#home';
                    return;
                }

                selectedOptionsArray = {};
                $scope.question=data[0];

                console.log("In QCTRL, gender : "+$rootScope.gender);
                if($scope.question.questionTarget != $rootScope.gender && $scope.question.questionTarget != 1){
                    lastQuestionId = $scope.question.id;
                    loadQuestion();
                    return;
                }

                if($scope.question && $scope.question.responses){
                    console.log("QCTRL ::::::: "+$scope.question.responses);
                    selectedOptions = $scope.question.responses.split(",");
                }

                for(i=0; i<$scope.question.options.length; i++){
                    var tempId = $scope.question.options[i].id;
                    if(!selectedOptions){
                        continue;
                    }
                    index = contains(selectedOptions, tempId);
                    if(index > -1){
                         selectedOptionsArray[tempId] = 1;
                         if($scope.question.responseType === 3){
                         $scope.question.options[i].description = "("+(index+1)+") "+$scope.question.options[i].description;
                    }
                    }else{
                         selectedOptionsArray[tempId] = 0;
                    }
                }
                responseType=$scope.question.responseType;
                lastQuestionId = $scope.question.id;
            });
            res.error(function(data, status, headers, config) {
            	alert( "failure message: " + JSON.stringify({data: data}));
            });
    }
    loadQuestion();

    $scope.isActive = function(optionId){
        if(selectedOptionsArray[optionId] == 1){
            return true;
        }else{
            return false;
        }
    }

    $scope.submitDetails = function(optionId){
        console.log("Selected optionID : "+optionId);
        if(!responseChanged){
            selectedOptionsArray = {};
            responseChanged=true;
        }
        if(responseType == 2){
            //selectedOptions = optionId;
            selectedOptionsArray = {};
            selectedOptionsArray[optionId] = 1;
        }else{
            if(selectedOptionsArray[optionId] == 0){
                selectedOptionsArray[optionId] = 1;
            }else{
                selectedOptionsArray[optionId] = 0;
            }
        }

    }

    $scope.nexButtonClicked = function(){
        if( $scope.addlText && $scope.addlText.length > 0 ){
            selectedOptionsArray[99] = 1;
        }

        if( !responseChanged && selectedOptionsArray[99]!=1){
            selectedOptions="";
            $scope.addlText=null;
            loadQuestion();
            return;
        }

        for (var opt in selectedOptionsArray){
            if(selectedOptionsArray[opt] == 1){
                selectedOptions = selectedOptions + "," + opt;
            }
        }
        selectedOptions = selectedOptions.substring(1);
        console.log("Consolidated SelectedOptions : "+selectedOptions);
        var dataObj = {
                        question : lastQuestionId,
                        options : selectedOptions,
                        comments : $scope.addlText,
                      };
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/response?ru='+$rootScope.userId, dataObj);
                  res.success(function(data, status, headers, config) {
                    selectedOptions="";
                    $scope.addlText=null;
                   	loadQuestion();
                  });
                  res.error(function(data, status, headers, config) {
                   	alert( "failure message: " + JSON.stringify({data: data}));
                  });
    }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('reportCtrl', function($scope, $http, $rootScope, $window, $route) {
    console.log("In report Controller!");
    var lastQuestionId = -1;
    var question;
    var responseType=2;
    var selectedOptionsArray = {};

    $scope.$on('$destroy', function(){
        $rootScope.userId = $rootScope.loggedInUserId;
    });

    var contains = function(a, obj) {
        var i = a.length;
        while (i--) {
           if (a[i] == obj) {
               return i;
           }
        }
        return -1;
    }
    var loadQuestion = function(){
       var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/responseDetail?lastQuestionId='+lastQuestionId+'&pageSize=1&ru='+$rootScope.userId);
            res.success(function(data, status, headers, config) {
                if(status === 204){
                    $scope.question.questionDescription="That's all for now! Please come back later to find out more about this person..."
                    //$window.location.href = '#home';
                }

                $scope.question=data[0];
                selectedOptions = $scope.question.responses.split(",");
                console.log("RESPONSES : "+$scope.question.responses+", arrayLength : "+selectedOptions.indexOf(1));
                selectedOptionsArray = {};

                for(i=0; i<$scope.question.options.length; i++){
                    var tempId = $scope.question.options[i].id;
                    index = contains(selectedOptions, tempId);
                    if(index > -1){
                        selectedOptionsArray[tempId] = 1;
                        if($scope.question.responseType === 3){
                            $scope.question.options[i].description = "("+(index+1)+") "+$scope.question.options[i].description;
                        }
                    }else{
                        selectedOptionsArray[tempId] = 0;
                    }

                }
                responseType=$scope.question.responseType;
                lastQuestionId = $scope.question.id;
            });
            res.error(function(data, status, headers, config) {
            	alert( "failure message: " + JSON.stringify({data: data}));
            });
    }
    loadQuestion();

    $scope.isActive = function(optionId){
        if(selectedOptionsArray[optionId] == 1){
            return true;
        }else{
            return false;
        }
    }

    $scope.nextButtonClicked = function(){
        selectedOptions="";
        $scope.addlText=null;
        loadQuestion();
    }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('scrollReportCtrl', function($scope, $http, $rootScope, $window, $route) {
    console.log("In scrollReport Controller!");
    var lastQuestionId = -1;
    var question;
    var responseType=2;
    var selectedOptionsArray = {};

    $rootScope.$on('$destroy', function(){
        $rootScope.userId = $rootScope.loggedInUserId;
        console.log("ScrollReportCtrl context destroyed!!! $rootScope.userId = "+$rootScope.userId);
    });

    $rootScope.$on('nextUserLoaded', function(){
        loadQuestion();
    });

    var contains = function(a, obj) {
        var i = a.length;
        while (i--) {
           if (a[i] == obj) {
               return i;
           }
        }
        return -1;
    }

    /*var profileData;
    var loadProfile = function(){
        if($rootScope.searchUserId == null || !$rootScope.searchUserId){
            console.log("SCROLLREPORTCTRL : searchUserId null or empty!");
            return;
        }
        console.log("SCROLLREPORTCTRL : $rootScope.searchUserId "+$rootScope.searchUserId);
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/profile');
        res.success(function(data, status, headers, config) {
            $rootScope.profileData = data;
            console.log("PROFILE DOB: "+data.name+" --- "+$rootScope.profileData.dateOfBirth);
        });
        res.error(function(data, status, headers, config) {
            alert( "failure message: " + JSON.stringify({data: data}));
        });

    }
    loadProfile();*/

    var loadQuestion = function(){

        if($rootScope.searchUserId == null || !$rootScope.searchUserId){
              console.log("SCROLLREPORTCTRL : searchUserId null or empty!");
              return;
        }

        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/responseDetail?lastQuestionId='+lastQuestionId+'&pageSize=999&ru='+$rootScope.userId);
            res.success(function(data, status, headers, config) {
                if(status === 204){
                    $scope.questions = {};
                    $scope.questions[0].questionDescription="That's all for now! Please come back later to find out more about this person..."
                    return;
                    //$window.location.href = '#home';
                }

                $scope.questions=data;
                selectedOptionsArray = {};

                for(j=0; j<$scope.questions.length; j++){
                    var quesObj = $scope.questions[j];
                    selectedOptions = quesObj.responses.split(",");
                    for(i=0; i<quesObj.options.length; i++){
                        var tempId = quesObj.options[i].id;
                        index = contains(selectedOptions, tempId);
                        if(index > -1){
                            selectedOptionsArray[tempId] = 1;
                            if(quesObj.responseType === 3){
                                quesObj.options[i].description = "("+(index+1)+") "+quesObj.options[i].description;
                        }
                        }else{
                            selectedOptionsArray[tempId] = 0;
                        }
                    }
                }
            });
            res.error(function(data, status, headers, config) {
            	alert( "failure message: " + JSON.stringify({data: data}));
            });
    }
    loadQuestion();

    $scope.isActive = function(optionId){
        if(selectedOptionsArray[optionId] == 1){
            return true;
        }else{
            return false;
        }
    }

    $scope.nextButtonClicked = function(){
        selectedOptions="";
        $scope.addlText=null;
        loadQuestion();
    }
    /*Image gallery*/

    /*var loadPictures = function(){
                if($rootScope.searchUserId == null || !$rootScope.searchUserId){
                    console.log("SCROLLREPORTCTRL : searchUserId null or empty!");
                    return;
                }
                console.log("SCROLLREPORTCTRL : $rootScope.searchUserId "+$rootScope.searchUserId);
                var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/images');
                    res.success(function(data, status, headers, config) {
                        $rootScope.photos = data;
                    });
                    res.error(function(data, status, headers, config) {
                        alert( "failure message: " + JSON.stringify({data: data}));
                    });

        }
        loadPictures();

    // initial image index
        $scope._Index = 0;

        // if a current image is the same as requested image
        $scope.isGalleryActive = function (index) {
            return $scope._Index === index;
        };

        // show prev image
        $scope.showPrev = function () {
            $scope._Index = ($scope._Index > 0) ? --$scope._Index : $scope.photos.length - 1;
        };

        // show next image
        $scope.showNext = function () {
            $scope._Index = ($scope._Index < $scope.photos.length - 1) ? ++$scope._Index : 0;
        };

        // show a certain image
        $scope.showPhoto = function (index) {
            $scope._Index = index;
        };*/
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('basicInfoCtrl', function($scope, $http, $rootScope, $window, $route) {
    console.log("In basicInfoCtrl Controller!");
    var lastQuestionId = -1;
    var question;
    var responseType=2;
    var selectedOptionsArray = {};

    $rootScope.$on('$destroy', function(){
        $rootScope.userId = $rootScope.loggedInUserId;
        console.log("ScrollReportCtrl context destroyed!!! $rootScope.userId = "+$rootScope.userId);
    });

    $rootScope.$on('nextUserLoaded', function(){
        loadProfile();
        loadPictures();
    });

    var contains = function(a, obj) {
        var i = a.length;
        while (i--) {
           if (a[i] == obj) {
               return i;
           }
        }
        return -1;
    }

    var profileData;
    var loadProfile = function(){
        if($rootScope.searchUserId == null || !$rootScope.searchUserId){
            console.log("SCROLLREPORTCTRL : searchUserId null or empty!");
            return;
        }


        var createUserRes = $http.post('/spouzee-api/v1/users', $rootScope.createUserDataObj);
        var uploadTokenRes;
        createUserRes.error(function(data, status, headers, config) {
            console.log( "Failed to create user : "+status );
        });
        createUserRes.success(function(data, status, headers, config) {
            $rootScope.sessionId = headers('sid');
            $http.defaults.headers.common.sid = $rootScope.sessionId;
        });

        console.log("SCROLLREPORTCTRL : $rootScope.searchUserId "+$rootScope.searchUserId);
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/profile?ru='+$rootScope.userId);
        res.success(function(data, status, headers, config) {
            $rootScope.profileData = data;
            console.log("PROFILE DOB: "+data.name+" --- "+$rootScope.profileData.dateOfBirth);
        });
        res.error(function(data, status, headers, config) {
            alert( "failure message: " + JSON.stringify({data: data}));
        });

    }
    loadProfile();

    $scope.isActive = function(optionId){
        if(selectedOptionsArray[optionId] == 1){
            return true;
        }else{
            return false;
        }
    }

    $scope.nextButtonClicked = function(){
        selectedOptions="";
        $scope.addlText=null;
        loadQuestion();
    }
    /*Image gallery*/

    var loadPictures = function(){
                if($rootScope.searchUserId == null || !$rootScope.searchUserId){
                    console.log("SCROLLREPORTCTRL : searchUserId null or empty!");
                    return;
                }
                console.log("SCROLLREPORTCTRL : $rootScope.searchUserId "+$rootScope.searchUserId);
                var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/images?ru='+$rootScope.userId);
                    res.success(function(data, status, headers, config) {
                        $rootScope.photos = data;
                    });
                    res.error(function(data, status, headers, config) {
                        alert( "failure message: " + JSON.stringify({data: data}));
                    });

        }
        loadPictures();

    // initial image index
        $scope._Index = 0;

        // if a current image is the same as requested image
        $scope.isGalleryActive = function (index) {
            return $scope._Index === index;
        };

        // show prev image
        $scope.showPrev = function () {
            $scope._Index = ($scope._Index > 0) ? --$scope._Index : $scope.photos.length - 1;
        };

        // show next image
        $scope.showNext = function () {
            $scope._Index = ($scope._Index < $scope.photos.length - 1) ? ++$scope._Index : 0;
        };

        // show a certain image
        $scope.showPhoto = function (index) {
            $scope._Index = index;
        };
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('profileCtrl', function($scope, $http, $rootScope, $window) {
    console.log("In ProfileCtrl");
    var profileData;
    var loadProfile = function(){
                var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/profile?ru='+$rootScope.userId);
                res.success(function(data, status, headers, config) {
                    $rootScope.profileData = data;
                    console.log("Name : "+data.name+" --- "+$rootScope.profileData.name);
                });
                res.error(function(data, status, headers, config) {
                    alert( "failure message: " + JSON.stringify({data: data}));
                });

        }
        loadProfile();
        $rootScope.$on("userLoggedIn", function (event, args) {
            $rootScope.showProfile=true;
            loadProfile();
        });

});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('headerCtrl', function($scope, $http, $rootScope, $window) {
    var matches = [];

    (function(d, s, id) {
                var js, fjs = d.getElementsByTagName(s)[0];
                if (d.getElementById(id)) return;
                js = d.createElement(s); js.id = id;
                js.src = "//connect.facebook.net/en_US/sdk.js";
                fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));

            $window.fbAsyncInit = function(){
                var fbAppId;
                if(document.location.hostname === "localhost"){
                    fbAppId = '543641319120330';
                }else{
                    fbAppId = '469873509830445';
                }
                FB.init({
                    appId      : fbAppId,
                    cookie     : true,  // enable cookies to allow the server to access
                                        // the session
                    xfbml      : true,  // parse social plugins on this page
                    version    : 'v2.2' // use version 2.2
                });

            // This is called with the results from from FB.getLoginStatus().
            $window.statusChangeCallback = function(response) {
                $rootScope.loginResponse = response;
                console.log(response);
                // The response object is returned with a status field that lets the
                // app know the current login status of the person.
                // Full docs on the response object can be found in the documentation
                // for FB.getLoginStatus().
                if (response.status === 'connected') {
                    // Logged into your app and Facebook.
                    console.log('Access Token : ' + response.authResponse.accessToken);
                    console.log('Facebook UserID : ' + response.authResponse.userID);
                    console.log('AccessToken expiration time : ' + response.authResponse.expiresIn);
                    var emailId=null;
                    FB.api('/me', function(detailsRes) {
                        $rootScope.meResponse = detailsRes;
                        console.log('Successful login for : ' + detailsRes.email);
                        if(detailsRes.gender == "male"){
                            $rootScope.gender = 2;
                        }else if(detailsRes.gender == "female"){
                            $rootScope.gender = 3;
                        }
                        console.log('Successful login for : ' + detailsRes.name + ', gender : '+$rootScope.gender);

                        /*$rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
                        console.log("$rootScope.loginStatus : "+$rootScope.loginStatus);
                        if($rootScope.loginStatus == 200){
                            $rootScope.profileCreated=true;
                            $window.location.href = '#match';
                        }else{
                            $window.location.href = '#details';
                        }*/
                        $rootScope.createUserDataObj = {
                            email : $rootScope.meResponse.email,
                            linkType : 'FACEBOOK',
                            accessToken : $rootScope.loginResponse.authResponse.accessToken,
                            linkTypeUserID : $rootScope.loginResponse.authResponse.userID,
                            createdTime : null,
                            tokenValidityInMillis : $rootScope.loginResponse.authResponse.expiresIn * 1000
                        }

                        var createUserRes = $http.post('/spouzee-api/v1/users', $rootScope.createUserDataObj);

                        var uploadTokenRes;
                        createUserRes.error(function(data, status, headers, config) {
        			        console.log( "Failed to create user" );
                        });
    		            createUserRes.success(function(data, status, headers, config) {
    		                $rootScope.userCreated=true;
    		                var locationTokensArray = headers('Location').split('/');
                            var userId = locationTokensArray[locationTokensArray.length - 1];
                            var createUserResponseStatus = status;
                            $rootScope.userId = userId;
                            $rootScope.sessionId = headers('sid');
                            /*$httpProvider.defaults.headers.common = { 'sid' : '$rootScope.sessionId' };*/
                            $http.defaults.headers.common.sid = $rootScope.sessionId;
                            /*$http.defaults.headers.get.sid = $rootScope.sessionId;*/
                            console.log("Create User response status : "+createUserResponseStatus);
                            $rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
                            if(createUserResponseStatus == 200){
                                $rootScope.profileCreated=true;
                                $window.location.href = '#match';
                            }else{
                                $window.location.href = '#details';
                            }
    		                /*console.log("Create User status : "+status+", location header : "+ headers('Location'));
    		                var locationTokensArray = headers('Location').split('/');
    		                var userId = locationTokensArray[locationTokensArray.length - 1];
    		                var createUserResponseStatus = status;
                            $rootScope.userId = userId;
                            globalUserId=userId;
                            console.log("rootScope userId : "+$rootScope.userId);
    		                var uploadTokenDataObj = {
                                linkType : 'FACEBOOK',
                                accessToken : response.authResponse.accessToken,
                                linkTypeUserID : response.authResponse.userID,
                                createdTime : null,
                                tokenValidityInMillis : response.authResponse.expiresIn * 1000
                            }

                            uploadTokenRes = $http.post('/spouzee-api/v1/users/'+userId+'/token', uploadTokenDataObj);
                            uploadTokenRes.success(function(data, status, headers, config) {
                                $rootScope.userCreated=true;
                                console.log("Create User response status : "+createUserResponseStatus);
                                $rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
                                if(createUserResponseStatus == 200){
                                    $rootScope.profileCreated=true;
                                    $window.location.href = '#match';
                                }else{
                                    $window.location.href = '#details';
                                }

                            });
                            uploadTokenRes.error(function(data, status, headers, config) {
        			            alert( "Failed to upload token" );
                            });*/
        		        });
                    });
                    console.log('Outside ME : Successful login for email : ' + emailId);




                } else if (response.status === 'not_authorized') {
                    // The person is logged into Facebook, but not your app.
                    console.log('Logged in to Facebook but app not authorized!');
                } else {
                    // The person is not logged into Facebook, so we're not sure if
                    // they are logged into this app or not.
                    console.log('Not logged into Facebook!');
                    setTimeout(function () {
                            $scope.$apply(function(){
                                $scope.showLogin = true;
                            });
                        }, 100);
                }
            }

            $scope.fbLogoutUser = function() {
                FB.getLoginStatus(function(response) {
                    if (response && response.status === 'connected') {
                        FB.logout(function(response) {
                            console.log("Logged out user...");
                            if(document.location.hostname === "localhost"){
                                $window.location.href = '/spouzee-api/';
                            }else{
                                $window.location.href = '/';
                            }
                        });
                    }
                });
            }

            $scope.checkLoginState = function() {
                console.log('In login method..');
                FB.login(function(response) {
                    statusChangeCallback(response);
                }, {scope: 'public_profile,email,user_birthday'}
                );
            }

            /*$scope.email = "";
            $scope.password = "";
            $scope.postCredentials = function(){
                var dataObj = {
    				email : $scope.email,
    				password : $scope.password
    		    };
    		    var res = $http.post('/spouzee-api/v1/users', dataObj);
    		    res.success(function(data, status, headers, config) {
    		        var locationTokensArray = headers('Location').split('/');
    			    $window.location.href = '#details';
    		    });
    		    res.error(function(data, status, headers, config) {
    			    alert( "failure message: " + JSON.stringify({data: data}));
                });
            }*/
}

    /*$rootScope.spzLogin = function(){
        var createUserDataObj = {
            email : $rootScope.meResponse.email,
            linkType : 'FACEBOOK',
            accessToken : $rootScope.loginResponse.authResponse.accessToken,
            linkTypeUserID : $rootScope.loginResponse.authResponse.userID,
            createdTime : null,
            tokenValidityInMillis : $rootScope.loginResponse.authResponse.expiresIn * 1000
        }

        var createUserRes = $http.post('/spouzee-api/v1/users', createUserDataObj);

        createUserRes.error(function(data, status, headers, config) {
            console.log( "Failed to create user" );
        });
        createUserRes.success(function(data, status, headers, config) {
            var locationTokensArray = headers('Location').split('/');
            var userId = locationTokensArray[locationTokensArray.length - 1];
            $rootScope.loginStatus = status;
            $rootScope.userId = userId;
            $rootScope.sessionId = headers('sid');
            console.log("Create User response status : "+$rootScope.loginStatus);
            $rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
        });
    }*/
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('inviteCtrl', function($scope, $http, $rootScope, $window) {
    $scope.inviteSent = false;
    console.log("In inviteCtrl");
    $scope.showInviteDialog = false;
    $scope.searchByEmailId = function(){
        $rootScope.searchByEmail=$scope.searchByEmail;
            console.log("In searchByEmail!");
            var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/search?email='+$scope.searchByEmail+'&ru='+$rootScope.userId);
            res.success(function(data, status, headers, config) {
                var locationTokensArray = headers('Location').split('/');
                var searchUId = locationTokensArray[locationTokensArray.length - 1];
                $rootScope.searchUserId = searchUId;
                console.log("searchUId : "+$rootScope.searchUserId);
                $window.location.href = '#reportScr';
            });
            res.error(function(data, status, headers, config) {
                if(status == 404){
                    $scope.showInviteDialog = true;
                    return;
                }
            });
        }

    $scope.sendInvite = function(){
        console.log("In sendInvite!");
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/invite?email='+$rootScope.searchByEmail+'&ru='+$rootScope.userId);
        res.success(function(data, status, headers, config) {
            $scope.inviteSent = true;
        });
        res.error(function(data, status, headers, config) {
            if(status == 404){
                $window.location.href = '#invite';
            }
        });
    }

    $scope.dontSendInvite = function(){
        $window.location.href = '#match';
    }

});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('expectICtrl', function($scope, $http, $rootScope, $window) {

    $scope.createQuestionnaire = function(){
        console.log("In createQuestionnaire");
        $window.location.href = '#expect';
    }

    $scope.skipCreateQuestionnaire = function(){
        console.log("In skipCreateQuestionnaire");
        $window.location.href = '#questions';
    }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('expectCtrl', function($scope, $http, $rootScope, $window, $route) {
    $scope.options = [];
    $scope.selected = [];
    var lastQuestionId = -1;
    var createdQuestionId = -1;
    var qIdToPull;
    var question;
    var responseType=4;
    var selectedOptions;
    var selectedOptionsArray = {};
    $rootScope.createQuestion=false;
    var loadQuestion = function(){
        qIdToPull = createdQuestionId > -1 ? (createdQuestionId - 1) : lastQuestionId;
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/questions?lastQuestionId='+qIdToPull+'&pageSize=1&ru='+$rootScope.userId);
            res.success(function(data, status, headers, config) {
                if(status === 202){
                    $scope.question="That's all for now! Please come back later to tell us more about you..."
                    $window.location.href = '#questions';
                }
                selectedOptions = "";
                selectedOptionsArray = {};
                $scope.question=data[0];

                for(i=0; i<$scope.question.options.length; i++){
                    selectedOptionsArray[$scope.question.options[i].id] = 0;
                }
                responseType=$scope.question.responseType;

                if(createdQuestionId > -1){
                    createdQuestionId = -1;
                    qIdToPull = $scope.question.id;
                }else{
                    qIdToPull = $scope.question.id;
                    lastQuestionId = $scope.question.id;
                }

            });
            res.error(function(data, status, headers, config) {
            	alert( "failure message: " + JSON.stringify({data: data}));
            });
    }
    loadQuestion();

    $scope.isActive = function(optionId){
        if(selectedOptionsArray[optionId] == 1){
            return true;
        }else{
            return false;
        }
    }

    $scope.submitDetails = function(optionId){
        console.log("Selected optionID : "+optionId);
        if(responseType == 2){
            //selectedOptions = optionId;
            selectedOptionsArray = {};
            selectedOptionsArray[optionId] = 1;
        }else{
            if(selectedOptionsArray[optionId] == 0){
                selectedOptionsArray[optionId] = 1;
            }else{
                selectedOptionsArray[optionId] = 0;
            }
        }

    }

    $scope.addToQuestionnaire = function(){
        if( $scope.addlText && $scope.addlText.length > 0 ){
            selectedOptionsArray[99] = 1;
        }

        for (var opt in selectedOptionsArray){
            if(selectedOptionsArray[opt] == 1){
                selectedOptions = selectedOptions + "," + opt;
            }
        }
        selectedOptions = selectedOptions.substring(1);
        console.log("Consolidated SelectedOptions : "+selectedOptions);
        var dataObj = {
                        question : qIdToPull,
                        options : selectedOptions,
                        comments : $scope.addlText,
                      };
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/expectation?ru='+$rootScope.userId, dataObj);
                  res.success(function(data, status, headers, config) {
                    selectedOptions="";
                    $scope.addlText=null;
                   	loadQuestion();
                  });
                  res.error(function(data, status, headers, config) {
                   	alert( "failure message: " + JSON.stringify({data: data}));
                  });
    }

    $scope.nexButtonClicked = function(){
        selectedOptions="";
        $scope.addlText=null;
        loadQuestion();
    }

    $scope.createNewQuestion = function(){
        $rootScope.createQuestion=true;
    }

    $scope.saveQuestion = function(){
        var dataObj = {
                       question : $scope.qDesc,
                       options : $scope.options,
                       responseType : $scope.responseType,
                       targetRole : 1
                      };
        console.log("UserQuestion : "+JSON.stringify({data: dataObj}));
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/questions?ru='+$rootScope.userId, dataObj);
        res.success(function(data, status, headers, config) {
            createdQuestionId = headers('Location');
            $rootScope.createQuestion = false;
            loadQuestion();
        });
        res.error(function(data, status, headers, config) {
            alert( "failure message: " + JSON.stringify({data: data}));
        });
    }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('browseCtrl', function($scope, $http, $rootScope, $window) {
    var matches = [];
    var selectedOptionsArray = {};

    $rootScope.$on('$destroy', function(){
        $rootScope.userId = $rootScope.loggedInUserId;
        console.log("BROWSEMATCH context destroyed!!! $rootScope.userId = "+ $rootScope.userId );
    });

    var getMatchStatus = function(){
        if($rootScope.matches == null){
                    $scope.displayExpressInterestButton = false;
                    $scope.displayRespondToInterestButton = false;
                    return;
        }
        for (i = 0; i < $rootScope.matches.length; i++) {
            var u = $rootScope.matches[i];
            if(u.userId == $rootScope.searchUserId){
                if(u.interestExpressed == true && u.respondedToInterest == false){
                    $scope.displayRespondToInterestButton = true;
                    $scope.displayExpressInterestButton = false;
                    $scope.displayShowContactButton = false;
                }else if(u.interestExpressed == false && u.otherUserExpressedInterest == false){
                    $scope.displayExpressInterestButton = true;
                    $scope.displayRespondToInterestButton = false;
                    $scope.displayShowContactButton = false;
                }else if( (u.otherUserExpressedInterest == true && u.irespondedToOtherUsersInterest == true) ||
                           (u.interestExpressed == true && u.respondedToInterest == true) ){
                    $scope.displayShowContactButton = true;
                    $scope.displayRespondToInterestButton = false;
                    $scope.displayExpressInterestButton = false;
                }
                break;
            }
        }
    }

    var afterMatchFetch = function(){
            $rootScope.loggedInUserId = $rootScope.userId;
            if($scope.matches != null && $scope.matches.length > 0){
                  $rootScope.searchUserId = $scope.matches[0].userId;
                  $rootScope.currMatchId = $scope.matches[0].matchId;
                  console.log("MatchUSERID : "+$scope.matches[0].userId);
                  $rootScope.searchUserId = $scope.matches[0].userId;
                  $rootScope.$broadcast("nextUserLoaded");
                  getMatchStatus();
                  $rootScope.activeTab = "/spouzee-api/basicInfo.html";
            }
        }

    var fetchMatches = function(){
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/match?ru='+$rootScope.userId);
        res.success(function(data, status, headers, config) {
            console.log("Matches : "+JSON.stringify({data: data}));
            $rootScope.matches = data;
            if($rootScope.matches.length == 0){
                $window.location.href = '#questions';
            }
            afterMatchFetch();
        });
        res.error(function(data, status, headers, config) {
            if(status == 404){
                alert("No Matches Found!");
            }
        });
    }

    console.log("BCTRL :: $rootScope.userId : "+$rootScope.userId);
    if($rootScope.matches == null || !$rootScope.matches){
        fetchMatches();
    }else{
        afterMatchFetch();
    }

    $scope.isActive = function(optionId){
        if(selectedOptionsArray[optionId] == 1){
            return true;
        }else{
            return false;
        }
    }



        $scope.showInterest = function(intUserId){
            var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/interest?otherUserId='+intUserId+'&ru='+$rootScope.userId);
                    res.success(function(data, status, headers, config) {
                        $scope.interestSent = true;
                        fetchMatches();
                    });
                    res.error(function(data, status, headers, config) {
                        alert("Something went wrong! Please try again later...");
                    });
        }

        $scope.respondToInterest = function(intUserId){
                var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/interestResponse?otherUserId='+intUserId+'&ru='+$rootScope.userId);
                res.success(function(data, status, headers, config) {
                    console.log("Identity revealed!");
                    fetchMatches();
                });
                res.error(function(data, status, headers, config) {
                    alert("Something went wrong! Please try again later...");
                });
        }

        $scope.requestContact = function(intUserId){
                        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/requestContact?otherUserId='+intUserId+'&ru='+$rootScope.userId);
                        res.success(function(data, status, headers, config) {
                            console.log("Contact requested!");
                            fetchMatches();
                        });
                        res.error(function(data, status, headers, config) {
                            alert("Something went wrong! Please try again later...");
                        });
        }

        $scope.revealContact = function(intUserId){
                        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/revealContact?otherUserId='+intUserId+'&ru='+$rootScope.userId);
                        res.success(function(data, status, headers, config) {
                            $rootScope.emailOfInterest = data.email;
                            alert("You can contact this person at "+$rootScope.emailOfInterest+
                            ". Hope this is the start of new beginnings! "+
                            "We would love to know if we made a difference to you! Keep us posted :)");
                            fetchMatches();
                        });
                        res.error(function(data, status, headers, config) {
                            alert("Something went wrong! Please try again later...");
                        });
        }

        $scope.showContact = function(intUserId){
                    var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/contact?otherUserId='+intUserId+'&ru='+$rootScope.userId);
                    res.success(function(data, status, headers, config) {
                        $rootScope.emailOfInterest = data.email;
                        alert("You can contact this person at "+$rootScope.emailOfInterest+
                        ". Hope this is the start of new beginnings! "+
                        "We would love to know if we made a difference to you! Keep us posted :)");
                    });
                    res.error(function(data, status, headers, config) {
                        alert("Something went wrong! Please try again later...");
                    });
            }

    $scope.viewMatch = function(matchUserId, cmId){
        console.log("In viewMatch");
        $rootScope.searchUserId = matchUserId;
        $rootScope.currMatchId = cmId;

        $rootScope.activeTab = "/spouzee-api/basicInfo.html";

        for (var opt in selectedOptionsArray){
            if(opt == matchUserId){
                selectedOptionsArray[opt]=1;
            }else{
                selectedOptionsArray[opt]=0;
            }
        }
        $rootScope.$broadcast("nextUserLoaded");
    }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('userDetailsCtrl', function($scope, $http, $rootScope, $window, $route) {

    $scope.onClickTab = function(tab) {
        console.log("Tab : "+tab);
        if(tab == "Scenarios"){
           $rootScope.activeTab = "/spouzee-api/discussScenario.html";
       }else if(tab == "Questions"){
           $rootScope.activeTab = "/spouzee-api/reportScrolling.html";
       }else{
           $rootScope.activeTab = "/spouzee-api/basicInfo.html";
       }
    }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('scenarioDiscussionCtrl', function($scope, $http, $rootScope, $window, $route) {

    var loadDiscussion = function(){
                    var res = $http.get('/spouzee-api/v1/matches/'+$rootScope.currMatchId+'/discussion?scenarioId='+$scope.scenario.id+'&ru='+$rootScope.userId);
                        res.success(function(data, status, headers, config) {
                            if(status === 202){
                                $scope.question="That's all for now! Please come back later to tell us more about you..."
                                $window.location.href = '#questions';
                            }
                            selectedOptions = "";
                            selectedOptionsArray = {};
                            $scope.discussion=data;
                            console.log("Discussion : "+$scope.discussion);

                            for (i = 0; i < $scope.discussion.length; i++) {
                                if($scope.discussion[i].userId == $rootScope.searchUserId){
                                    $scope.discussion[i].bgcolor = "#99ccff";
                                    $scope.discussion[i].align = "right";
                                }else{
                                    $scope.discussion[i].bgcolor = "#ffffff";
                                    $scope.discussion[i].align = "left";
                                }
                            }
                        });
                        res.error(function(data, status, headers, config) {
                        	alert( "failure message: " + JSON.stringify({data: data}));
                        });
        }

    var loadScenario = function(){
            var res = $http.get('/spouzee-api/v1/matches/'+$rootScope.currMatchId+'/scenario?pageSize=1&ru='+$rootScope.userId);
                res.success(function(data, status, headers, config) {
                    if(status === 202){
                        $scope.question="That's all for now! Please come back later to tell us more about you..."
                        $window.location.href = '#questions';
                    }
                    selectedOptions = "";
                    selectedOptionsArray = {};
                    $scope.scenario=data[0];
                    console.log("Scenario : "+$scope.scenario.description);
                    loadDiscussion();
            });
            res.error(function(data, status, headers, config) {
                alert( "failure message: " + JSON.stringify({data: data}));
            });
    }
    loadScenario();

    $scope.postButtonClicked = function(){
            console.log("Consolidated SelectedOptions : "+selectedOptions);
            var dataObj = {
                            scenarioId : $scope.scenario.id,
                            userId : $rootScope.userId,
                            matchId: $rootScope.currMatchId,
                            message : $scope.message,
                          };
            var res = $http.post('/spouzee-api/v1/matches/'+$rootScope.currMatchId+'/discussion?ru='+$rootScope.userId, dataObj);
                      res.success(function(data, status, headers, config) {
                        loadDiscussion();
                        $scope.message = "";
                      });
                      res.error(function(data, status, headers, config) {
                       	alert( "failure message: " + JSON.stringify({data: data}));
                      });
        }

        $scope.nextScenarioRequested = function(){
                    console.log("Consolidated SelectedOptions : "+selectedOptions);
                    var dataObj = {
                                    scenarioId : $scope.scenario.id,
                                    userId : $rootScope.userId,
                                    matchId: $rootScope.currMatchId,
                                    message : $scope.message,
                                    complete : true
                                  };
                    var res = $http.post('/spouzee-api/v1/matches/'+$rootScope.currMatchId+'/discussion?ru='+$rootScope.userId, dataObj);
                              res.success(function(data, status, headers, config) {
                                loadScenario();
                                $scope.message = "";
                              });
                              res.error(function(data, status, headers, config) {
                               	alert( "failure message: " + JSON.stringify({data: data}));
                              });
                }
});