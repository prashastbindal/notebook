var currentFramePath = '';
var iframe = '<iframe src="{src}" name="iframe_a" id="iframe_a "frameborder="0" width="100%" height="100"></iframe>';

$(document).ready(function(){

    var urlFrame = getUrlParameter('currentFrame');

    if(urlFrame != null && urlFrame != ''){
        $('#iframeContainer').html(iframe.replace('{src}', urlFrame));
        currentFramePath = urlFrame;
    }

    $('.button').click(function(){
        currentFramePath = $(this).attr('href');
    });
});

function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};