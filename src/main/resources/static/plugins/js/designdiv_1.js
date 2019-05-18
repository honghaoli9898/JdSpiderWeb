function designdiv_1() {
    var myselect = document.getElementById("text");
    var index = myselect.selectedIndex;
    var a = myselect.options[index].text;
    if (a == '更多') {
        window.location.href = ('http://www.baidu.com/more/');
    }
}
function mouseshell(e){
    e=e || window.event;
    if(e.wheelDelta){//IE/Opera/Chrome
        //自定义事件：编写具体的实现逻辑
        console.log("wo");
        $(".div161").css("background-color","red")
        mouseScroll();
    }else if(e.detail){//Firefox
        //自定义事件：编写具体的实现逻辑
        console.log("men");
        mouseScroll();
    }
}
$(function () {
    var mystyle= $(".div100").prevUntil(".div1");
    for(var i=0;i<mystyle.length;i++){
        $(mystyle[i]).mouseover(function () {
            this.style.backgroundColor="aliceblue";
        });
        $(mystyle[i]).mouseout(function () {
            this.style.backgroundColor="";
        });
    }
    $(".div17").click(function () {
        window.location.href = ('http://www.baidu.com');
    });
    //固定div16在顶部  并且改变背景颜色
    var ie6 = document.all;
    var dv = $('.div161'), st;
    dv.attr('otop', dv.offset().top); //存储原来的距离顶部的距离
    $(window).scroll(function () {
        st = Math.max(document.body.scrollTop || document.documentElement.scrollTop);
        if (st > parseInt(dv.attr('otop'))) {
            if (dv.css('position') != 'fixed') {
                dv.css({ 'position': 'fixed', top: 0 });
                $('.div161').css("background-color","gray");
            };
        } else if (dv.css('position') != 'static') {
            dv.css({ 'position': 'static' });
                $('.div161').css("background-color","white");
        }
    });

});