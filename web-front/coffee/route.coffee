class AppRouter extends Backbone.Router

    
    initialize: =>
        @isLogin = false
        @nabaztagCollection = new NabaztagCollection()
        $('.showLogin').hide()
        $('.showLogout').hide()
    
    setIsLogin: (isLogin) =>
        @isLogin = isLogin
        if isLogin
            $('.showLogin').hide()
            $('.showLogout').show()
        else
            $('.showLogout').hide()
            $('.showLogin').show()
            
    
    routes: {
        "onLogin": "onLogin",
        "logout": "logout",
        "nabaztag/action/:id": "nabaztagAction",
        "nabaztag/list": "nabaztagList",
        "nabaztag/add": "nabaztagAdd",
        "*actions": "defaultRoute"
    }

    defaultRoute: ( actions ) =>
        console.log( actions )
        $('#content').html("")
        if(@isLogin)
            @navigate("nabaztag/list", true)

    onLogin: =>
        console.log("onLogin")
        @setIsLogin(true)
        @navigate("nabaztag/list", true)
        
    logout: =>
        console.log("logout")
        $.Storage.remove("token")
        @setIsLogin(false)
        @navigate("home", true)
        
    nabaztagList: =>
        console.log("nabaztagList: ", @nabaztagCollection)
        nabaztagCollectionView = new NabaztagCollectionView({model: @nabaztagCollection})
        $('#content').html($(nabaztagCollectionView.el))
        
    nabaztagAction: (id)=>
        @nabaztagCollection.getAndRun(id, (nab)=>
            console.log("nab",nab)
            nabaztagView = new NabaztagView({model: nab})
            $('#content').html($(nabaztagView.render().el))
        )

    nabaztagAdd: =>
        console.log("nabaztagAdd")
        nabaztagAddView = new NabaztagAddView()
        $('#content').html($(nabaztagAddView.render().el))


login = new LoginView()
$('#login').html($(login.render().el))

this.router = new AppRouter()
token = $.Storage.get("token");
if token
    $.getJSON('user/info', {"token": token})
    .success(=>
        this.router.setIsLogin(true)
        this.router.nabaztagCollection.fetch()
    )
    .error(=>
        this.router.setIsLogin(false)
        this.router.navigate("login", true)
    )
    .complete(=>
        Backbone.history.start()
    )
else
    this.router.setIsLogin(false)
    this.router.navigate("home", true)
    Backbone.history.start()
            

