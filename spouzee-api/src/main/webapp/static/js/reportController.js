app.controller('reportCtrl', function($scope, $http, $rootScope, $window) {
    //$rootScope.userId=111;
    //$rootScope.$broadcast("userLoggedIn", {user_id: $rootScope.userId});
    var nextIndex=0;
    var question;
    var responses = [];
    var loadResponses = function(){
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.searchUserId+'/responseSummary');
             res.success(function(data, status, headers, config) {
                 if(status == 204 || data.length == 0){
                     $scope.question="This user has not answered any questions so far! Please check after a while...";
                     //$window.location.href = '#home';
                     return;
                 }
                 $scope.responses=data;
                 //responseType=$scope.question.responseType;
                 //lastQuestionId = $scope.question.id;
                 console.log("Responses : "+JSON.stringify({data: data}));
                 resp = $scope.responses[nextIndex];
                 console.log("Question ID : "+resp.question);
                 loadQuestion(resp.question);
             });
             res.error(function(data, status, headers, config) {
                 alert( "failure message: " + JSON.stringify({data: data}));
             });
    };
    loadResponses();
    var loadQuestion = function(questionId){
        var res = $http.get('/spouzee-api/v1/users/'+$rootScope.userId+'/questions?lastQuestionId='+(questionId-1)+'&pageSize=1');
            res.success(function(data, status, headers, config) {
                if(status === 202){
                    $scope.question="That's all for now! Please come back later to tell us more about you..."
                    $window.location.href = '#home';
                }
                $scope.question=data[0];
            });
            res.error(function(data, status, headers, config) {
            	alert( "failure message: " + JSON.stringify({data: data}));
            });
    }


    $scope.isActive = function(optionId){
        resp = $scope.responses[nextIndex];
        optionsSelectedArr = resp.options.split(",");
        for(i=0; i<optionsSelectedArr.length; i++){
            if(optionsSelectedArr[i].id == optionId){
                return true;
            }
        }
        return false;
    }

    $scope.nextButtonClicked = function(){
        nextIndex = nextIndex + 1;
        if(nextIndex >= $scope.responses.length){
            alert("That's it!");
            return;
        }
        resp = $scope.responses[nextIndex];
        console.log("####Responses : "+responses.length);
        console.log("####Responses[1] : "+responses[1].question);
        console.log("Question ID : "+resp.question);
        loadQuestion(resp.question);
    }
});