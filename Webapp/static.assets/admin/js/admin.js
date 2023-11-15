const app = angular.module("admin-ctrl", []);
app.controller("shopping-cart-sell-ctrl", function($scope, $http) {
    $scope.voucher={
        voucherCode : localStorage.getItem('voucher_sell') || '',
        errorMessage :"",
        voucherPrice:0,
        isValid: 0 ,  // = 0 chưa nhập voucher, 1 là nhập đúng, 2 là nhập sai
        showMessage(message){
            alert(message)
            $scope.voucher.errorMessage=message
            $("#myModal").modal("hide");
        },
        getVoucher(code){
            if(code){
                $http.get(`/rest/voucher/code?code=`+code).then(resp => {
                    this.voucherPrice = resp.data.voucher_price ;
                    this.isValid = 1;
                }).catch(error => {
                    console.log(error)
                })
            }else {
                this.voucherPrice = 0

            }

        },
        saveVoucherCode(){
            $http.get(`/rest/voucher/vadidate?code=`+$scope.voucher.voucherCode).then(resp => {
                const isValidVoucher  =  resp.data;
                if(isValidVoucher!=1000){ // Voucher không hợp lệ
                    if(isValidVoucher==1001){
                        $scope.voucher.errorMessage = "Voucher không tồn tại."
                        this.voucherPrice = 0;
                        this.isValid = 2;
                    }else if(isValidVoucher==1002){
                        $scope.voucher.errorMessage = "Voucher hết hạn."
                        this.voucherPrice = 0;
                        this.isValid = 2;
                    }else{
                        $scope.voucher.errorMessage = "Voucher không hợp lệ."
                        this.voucherPrice = 0;
                        this.isValid = 2;
                    }
                }else{
                    this.isValid = 1;
                    $scope.voucher.errorMessage =  ""
                    localStorage.setItem("voucher_sell", this.voucherCode);
                    this.getVoucher(this.voucherCode);

                }
            }).catch(error => {
                console.log(error)
            })
        },
        clearVoucher(){
            localStorage.removeItem("voucher_sell");
        }
    }
    $scope.cart = {
        username:"",
        keySearch:"",
        products: [],
        currentPage:1,
        totalElements:1,
        totalPages:1,
        items: [],
        getitem : {} ,
        getcomment :[],
        get_orderid :0,
        prod : [] ,
        searchProduct(event){
            if (event.keyCode === 13) {
                this.getProducts(1)
            }
        },
        printBill(pathFile){
            console.log(pathFile)
            printJS({
                printable: "/external"+pathFile,
                type: "pdf",
                header: "Hóa đơn", // Tiêu đề trang in
                documentTitle: "Hoa-don.pdf", // Tên tài liệu PDF
            });
        },
        formatVND(number){
            var formattedVND = number.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
            return formattedVND;
        } ,
        add(product_id) {
            var item = this.items.find(item => (item.product_id == product_id ));

            if (item) {
                item.quantity++;
                this.saveToLocalStorage();
            } else {
                $http.get(`/rest/products/${product_id}`).then(resp => {
                    resp.data.quantity = 1;

                    this.items.push(resp.data);
                    this.saveToLocalStorage();
                })
            }
        },
        get_infoorderid(orderid){
            this.get_orderid = orderid;
            this.saveToLocalStorage();
        },
        getinfComment(product_id){
            $http.get(`/rest/comments/${product_id}`).then(resp => {
                this.getcomment = resp.data;
                $scope.isshowcomment = false;
                this.saveToLocalStorage();
            })
        },
        getinfoproducts(product_id){
            var item = this.items.find(item => (item.product_id == product_id ));
            $http.get(`/rest/products/${product_id}`).then(resp => {
                this.getitem = resp.data;
                this.saveToLocalStorage();
            })
        }
        ,
        maxquantity(product_id){
            $http.get(`/rest/products/${product_id}`).then(resp => {
                this.prod = resp.data;
            })
            return this.prod.quantity;
        }
        ,
        remove(product_id) {
            var index = this.items.findIndex(item => item.product_id == product_id);
            this.items.splice(index, 1);
            this.saveToLocalStorage();
        },
        clear() {
            this.items = [];
            this.username= "";
            this.keySearch= "";
            this.saveToLocalStorage();
            localStorage.removeItem("cart_sell");
        },
        amt_of(item) {},
        get count() {
            return this.items
                .map(item => item.quantity)
                .reduce((total, quantity) => total += quantity, 0);
        },
        get amount() {
            const amount = this.items
                .map(item => item.quantity*item.unit_price-((item.quantity*item.unit_price)*item.distcount/100))
                .reduce((total, quantity) => total += quantity, 0)
            return amount;
        },
        get amount1() {
            return this.items
                .map(item => item.quantity * item.unit_price)
                .reduce((total, quantity) => total += quantity, 0);
        },
        get amount2() {
            return this.items
                .map(item => (item.quantity * item.unit_price)-(item.quantity*item.unit_price-((item.quantity*item.unit_price)*item.distcount/100)))
                .reduce((total, quantity) => total += quantity, 0);
        },
        saveToLocalStorage() {
            var json = JSON.stringify(angular.copy(this.items));
            localStorage.setItem("cart_sell", json);

        },
        loadFromLocalStorage() {
            var json = localStorage.getItem("cart_sell");
            this.items = json ? JSON.parse(json) : [];
        },
        getProducts(pageNumber){
            const url = `/rest/products/list?pageNumber=${pageNumber-1}&pageSize=15&keySearch=${this.keySearch}`
            if(pageNumber>0 && pageNumber<=this.totalPages){
                $http.get(url).then(resp => {
                    this.products = resp.data.content
                    this.totalElements = resp.data.totalElements
                    this.currentPage = pageNumber
                    this.totalPages = resp.data.totalPages
                })
            }
        },
        nextPage(){
           this.getProducts(this.currentPage+1)
        },
        prevPage(){
           this.getProducts(this.currentPage-1)
        }

    }
    $scope.cart.getProducts(1);
    $scope.voucher.getVoucher(localStorage.getItem('voucher_sell') || '')
    $scope.cart.loadFromLocalStorage();
    $scope.order = {
        createDate: new Date(),
        address: "",
        phone : "",
        status : 0,
        intent: 'Sale',
        method: 'Trả sau',
        currency: 'VND',
        description: '',
        voucher_price:0,
        money_give:0,
        money_send:0,
        price : $scope.cart.amount,
        account: { username:""},
        get orderDetails() {
            return $scope.cart.items.map(item => {
                return {
                    product: item,
                    price: item.unit_price,
                    quantity: item.quantity
                }
            });
        },
        caculatorMoneySend(){
            const amount = $scope.cart.amount - $scope.voucher.voucherPrice;
            const moneySend = this.money_give - amount;
            const totalAmount = (moneySend > 0) ? moneySend: 0;
            this.money_send=  totalAmount;
        },
        clear(){
          this.address="";
          this.phone="";
          this.description="";
          this.money_give=0;
          $scope.voucher.isValid =0;
        },
        purchase() {

            var order = angular.copy(this);
            order.price = $scope.cart.amount;
            order.account.username = $scope.cart.username?$scope.cart.username:"Anonymous";
            order.voucher_price = $scope.voucher.voucherPrice;
            $http.post("/rest/orders/sell?code="+$scope.voucher.voucherCode, order).then(resp => {
                alert("Đặt hàng thành công");
                $scope.cart.clear();
                this.clear();
                $("#modal-checkout").modal("hide")
                $scope.cart.printBill(resp.data.pathFile)
                $scope.voucher.clearVoucher();
                $scope.voucher.voucherCode = "";
            }).catch(error => {
                if(error?.data) alert(error.data.data)
                else alert("Đặt hàng thất bại");
                console.log(error)
            })
        },
        purchase1() {
            var order = angular.copy(this);
            $http.post("/rest/orders", order).then(resp => {
            }).catch(error => {
                alert("Đặt hàng thất bại");
                console.log(error)
            })
        },
        purchase2() {
            $scope.cart.clear();
        }
    }




});

app.controller("authority", function($scope, $http , $location) {
	$scope.roles=[];
   $scope.admins = [];
   $scope.authorities = [];

   $scope.initialize = function(){
       // load all roles
       $http.get("/rest/roles").then(resp =>{
           $scope.roles = resp.data;
       })

       //load staffs and directors(administrators)
       $http.get("/rest/accounts").then(resp =>{
           $scope.admins = resp.data;
       })

       //load authorities of staffs and directors
       $http.get("/rest/authorities").then(resp =>{
            $scope.authorities = resp.data;
         }).catch(error =>{
             $location.path("/unauthorized");
         })
   }

   $scope.authority_of = function(acc , role){
       if($scope.authorities){
           return $scope.authorities.find(ur => ur.account.username == acc.username 
                                            && ur.role.role_id == role.role_id);
       }
   }
   $scope.authority_changed = function(acc,role){
       var authority = $scope.authority_of(acc , role);
       if(authority){
           $scope.revoke_authority(authority);
       }
       else{
           authority = {account:acc , role : role};
           $scope.grant_authority(authority);
       }
   }

   //thêm mới authority
   $scope.grant_authority = function(authority){
       $http.post(`/rest/authorities`, authority).then(resp =>{
           $scope.authorities.push(resp.data);
           alert("Cấp quyền sử dụng thành công");
       }).catch(error =>{
           alert("Cấp quyền thất bại");
            console.log("Error" , error);
       })
   }

   //Xoá authority
   $scope.revoke_authority = function(authority){
       $http.delete(`/rest/authorities/${authority.authorize_id}`).then(resp =>{
           var index = $scope.authorities.findIndex(a => a.Authorize_id == authority.authorize_id);
           $scope.authorities.splice(index ,1);
           alert("Thu hồi quyền sử dụng thành công");
       }).catch(error =>{
        alert("Thu hồi quyền sử dụng thất bại");
        console.log("Eror",error );
       })
   }

   $scope.initialize();
    $scope.pager = {
        page: 0,
        size: 10,
        get admins() {
            var start = this.page * this.size;
            return $scope.admins.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.admins.length / this.size);
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }
});
app.controller("trademark-ctrl", function($scope, $http) {
    $scope.items = [];

    $scope.form = {};

    $scope.initialize = function() {
        $http.get("/rest/trademarks").then(resp => {
            $scope.items = resp.data;
           
        });
    }
    $scope.initialize();
    $scope.reset = function() {
        $scope.form = {
            
        }
    }
    $scope.edit = function(item) {
        $scope.form = angular.copy(item);
       
    }
    $scope.create = function() {
        var item = angular.copy($scope.form);
        $http.post(`/rest/trademarks`, item).then(resp => {
            resp.data, createDate = new Date(resp.data.createDate);
            $scope.items.push(resp.data);
            $scope.reset();
            alert("Thêm mới thành công");
        }).catch(error => {
            alert("Thêm mới thất bại");
            console.log("Error", error);
        })
    }
    $scope.update = function(item) {
        var item = angular.copy($scope.form);
        $http.put(`/rest/trademarks/${item.trademark_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = item;
            alert("Cập nhập thành công");
        }).catch(error => {
            alert("Cập nhập thất bại");
            console.log("Error", error);
        })
    }
    $scope.delete = function(item) {
        $http.delete(`/rest/trademarks/${item.trademark_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            alert("Xóa thành công");
        }).catch(error => {
            alert("Không thể xóa , vì vẫn còn hàng");
            console.log("Error", error);
        })
    }
    $scope.pager = {
        page: 0,
        size: 6,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }
});
app.controller("category-ctrl", function($scope, $http) {
    $scope.items = [];

    $scope.form = {};

    $scope.initialize = function() {
        $http.get("/rest/categories").then(resp => {
            $scope.items = resp.data;
           
        });
    }
    $scope.initialize();
    $scope.reset = function() {
        $scope.form = {
            
        }
    }
    $scope.edit = function(item) {
        $scope.form = angular.copy(item);
       
    }
    $scope.create = function() {
        var item = angular.copy($scope.form);
        $http.post(`/rest/categories`, item).then(resp => {
            resp.data, createDate = new Date(resp.data.createDate);
            $scope.items.push(resp.data);
            $scope.reset();
            alert("Thêm mới thành công");
        }).catch(error => {
            alert("Lỗi thêm mới loại sản phẩm");
            console.log("Error", error);
        })
    }
    $scope.update = function(item) {
        var item = angular.copy($scope.form);
        $http.put(`/rest/categories/${item.category_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = item;
            alert("Cập nhập thành công");
        }).catch(error => {
            alert("Cập nhập thất bại");
            console.log("Error", error);
        })
    }
    $scope.delete = function(item) {
        $http.delete(`/rest/categories/${item.category_id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            alert("Xóa thành công");
        }).catch(error => {
            alert("Không thể xóa loại sản phẩm vì vẫn còn hàng trong kho");
            console.log("Error", error);
        })
    }
    $scope.pager = {
        page: 0,
        size: 6,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }
});

app.controller("orderstatus-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/ordersstatus").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
    $scope.edit = function(order_id) {
       location.href = "/admin/order/edit?order_id=" + order_id;
       
    }
    $scope.initialize();
    $scope.pager = {
        page: 0,
        size: 10,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
        },
        get sumorder() {
            return Math.ceil(1 * $scope.items.length);
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }
});
app.controller("charuser-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/report1").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
  
    $scope.initialize();
    $scope.pager = {
        page: 0,
        size: 5,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1 * $scope.items.length / this.size);
        },
        get sumorder() {
            return Math.ceil(1 * $scope.items.length);
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }
});

app.controller("iconadmin-ctrl", function($scope, $http) {
  
 	 $scope.product = function() {
       location.href = "/admin/product/list";
       
    }

    $scope.order = function() {
       location.href = "/admin/order/list";
       
    }
    
     $scope.account = function() {
       location.href = "/admin/account/list";
       
    }
    
     $scope.post = function() {
       location.href = "/admin/post/list";
       
    }
    
 });
 app.controller("ordertop10-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/ordertop10").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
    $scope.edit = function(order_id) {
       location.href = "/admin/order/edit?order_id=" + order_id;
       
    }
    $scope.initialize();
    
});
app.controller("producttop10-ctrl", function($scope, $http) {
    $scope.items = [];
 

    $scope.initialize = function() {
        $http.get("/rest/producttop10").then(resp => {
            $scope.items = resp.data;
            
        });
       
    }
    $scope.edit = function(product_id) {
       location.href = "/admin/product/edit?product_id=" + product_id;
       
    }
    $scope.initialize();
    
});