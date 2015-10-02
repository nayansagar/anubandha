var globalUserId;
var app = angular.module('spzApp', ['ngRoute']);
app.config(function($routeProvider){
  $routeProvider.when("/",
    {
      templateUrl: "/spouzee-api/login.html"
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
  ).when("/invite",
    {
      templateUrl: "/spouzee-api/invite.html",
      controller: "inviteCtrl"
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
  ).otherwise(
    {
      redirectTo: '/home'
    }
  );
});

app.controller('loginCtrl', function($scope, $http, $window, $rootScope) {

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
            console.log("FB APP ID : "+fbAppId);
            FB.init({
                appId      : fbAppId,
                cookie     : true,  // enable cookies to allow the server to access
                                    // the session
                xfbml      : true,  // parse social plugins on this page
                version    : 'v2.2' // use version 2.2
            });

            FB.getLoginStatus(function(response) {
                statusChangeCallback(response);
            });
        }

        // This is called with the results from from FB.getLoginStatus().
        $window.statusChangeCallback = function(response) {
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
                    console.log('Successful login for : ' + detailsRes.name);
                    console.log('Successful login for : ' + detailsRes.email);

                    var createUserDataObj = {
                        email : detailsRes.email,
                        password : ''
                    }

                    var createUserRes = $http.post('/spouzee-api/v1/users', createUserDataObj);

                    var uploadTokenRes;
                    createUserRes.error(function(data, status, headers, config) {
    			        console.log( "Failed to create user" );
                    });
		            createUserRes.success(function(data, status, headers, config) {
		                console.log("Create User status : "+status+", location header : "+ headers('Location'));
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
                            console.log("Create User response status : "+createUserResponseStatus);
                            if(createUserResponseStatus == 200){
                                $window.location.href = '#questions';
                            }else{
                                $window.location.href = '#details';
                            }

                        });
                        uploadTokenRes.error(function(data, status, headers, config) {
    			            alert( "Failed to upload token" );
                        });
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

        $scope.checkLoginState = function() {
            console.log('In login method..');
            FB.login(function(response) {
                statusChangeCallback(response);
            }, {scope: 'public_profile,email,user_birthday'}
            );
        }

        $scope.email = "";
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
        }

});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('detailsCtrl', function($scope, $http, $rootScope, $window) {
            console.log("rootScope userId : "+$rootScope.userId);
            $scope.submitDetails = function(){

                var dataObj = {
                				name : $scope.name,
                				dateOfBirth : $scope.dateOfBirth,
                				religion : $scope.religion,
                				caste : $scope.caste,
                				subcaste : $scope.subcaste,
                				language : $scope.language,
                				employment : $scope.employment,
                				qualification : $scope.qualification,
                				role : $scope.role
                		    };

                var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId, dataObj);
                res.success(function(data, status, headers, config) {
                	$window.location.href = '#expectI';
                });
                res.error(function(data, status, headers, config) {
                	alert( "failure message: " + JSON.stringify({data: data}));
                });

                console.log("Name : "+$scope.name);
            }
});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('questionsCtrl', function($scope, $http, $rootScope, $window, $route) {
    //$rootScope.userId=111;
    $rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
    var lastQuestionId = -1;
    var question;
    var responseType=2;
    var selectedOptions;
    var selectedOptionsArray = {};
    var loadQuestion = function(){
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/questions?lastQuestionId='+lastQuestionId+'&pageSize=1');
            res.success(function(data, status, headers, config) {
                if(status === 202){
                    $scope.question="That's all for now! Please come back later to tell us more about you..."
                    $window.location.href = '#home';
                }
                selectedOptions = "";
                selectedOptionsArray = {};
                $scope.question=data[0];

                for(i=0; i<$scope.question.options.length; i++){
                    selectedOptionsArray[$scope.question.options[i].id] = 0;
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
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/response', dataObj);
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
    //$rootScope.userId=111;
    //$rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
    var lastQuestionId = -1;
    var question;
    var responseType=2;
    var selectedOptionsArray = {};

    $scope.$on('$destroy', function(){
        $rootScope.userId = $rootScope.loggedInUserId;
    });

    var contains = function(a, obj) {
        //console.log("IN CONTAINS : array : "+a.toString()+", obj : "+obj);
        var i = a.length;
        while (i--) {
           //console.log("IN CONTAINS : array["+i+"] : " +a[i]);
           if (a[i] == obj) {
               return i;
           }
        }
        return -1;
    }
    var loadQuestion = function(){
        $rootScope.loggedInUserId = $rootScope.userId;
        $rootScope.userId = $rootScope.searchUserId;
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/responseDetail?lastQuestionId='+lastQuestionId+'&pageSize=1');
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
                    console.log("Index of option "+tempId+" : "+selectedOptions.indexOf(tempId));
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
            console.log("IsActive for optionId : "+optionId+" : returning true...");
            return true;
        }else{
            console.log("IsActive for optionId : "+optionId+" : returning false...");
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
app.controller('profileCtrl', function($scope, $http, $rootScope, $window) {
    //$rootScope.userId=111;
    //$rootScope.showProfile=false;
    console.log("In ProfileCtrl")
    var profileData;
    var loadProfile = function(){
                var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/profile');
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
    //$rootScope.userId=111;

    $scope.searchByEmailId = function(){
    $rootScope.searchByEmail=$scope.searchByEmail;
        console.log("In searchByEmail!");
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/search?email='+$scope.searchByEmail);
        res.success(function(data, status, headers, config) {
            var locationTokensArray = headers('Location').split('/');
            var searchUId = locationTokensArray[locationTokensArray.length - 1];
            $rootScope.searchUserId = searchUId;
            $window.location.href = '#report';
        });
        res.error(function(data, status, headers, config) {
            if(status == 404){
                $window.location.href = '#invite';
                return;
            }
        });
    }

    $rootScope.$on("userLoggedIn", function (event, args) {
        $rootScope.showProfile=true;
    });

});
/*----------------------------------------------------------------------------------------------------------------*/
/*----------------------------------------------------------------------------------------------------------------*/
app.controller('inviteCtrl', function($scope, $http, $rootScope, $window) {
    //$rootScope.userId=111;
    $scope.inviteSent = false;
    console.log("In inviteCtrl");
    $scope.showInviteDialog = false;
    $scope.searchByEmailId = function(){
        $rootScope.searchByEmail=$scope.searchByEmail;
            console.log("In searchByEmail!");
            var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/search?email='+$scope.searchByEmail);
            res.success(function(data, status, headers, config) {
                var locationTokensArray = headers('Location').split('/');
                var searchUId = locationTokensArray[locationTokensArray.length - 1];
                $rootScope.searchUserId = searchUId;
                $window.location.href = '#report';
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
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/invite?email='+$rootScope.searchByEmail);
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
        $window.location.href = '#questions';
    }

    $rootScope.$on("userLoggedIn", function (event, args) {
        $rootScope.showProfile=true;
    });

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
    //$rootScope.userId=111;
    $rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
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
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/questions?lastQuestionId='+qIdToPull+'&pageSize=1');
            res.success(function(data, status, headers, config) {
                if(status === 202){
                    $scope.question="That's all for now! Please come back later to tell us more about you..."
                    $window.location.href = '#home';
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
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/expectation', dataObj);
                  res.success(function(data, status, headers, config) {
                    /*if(createdQuestionId > -1){
                       createdQuestionId = -1;
                    }else{
                       lastQuestionId = qIdToPull;
                    }*/
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
        var res = $http.post('/spouzee-api/v1/users/'+$rootScope.userId+'/questions', dataObj);
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
