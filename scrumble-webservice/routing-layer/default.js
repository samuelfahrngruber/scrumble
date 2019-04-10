// packages
let express = require('express'),
oracleConnection = require('../data-layer/oracleDataAccess'),
    router = express.Router(),
    error = require('./misc/error');

// add routes
router.get('/', function (req, res) {
    res.json({
        title: 'Scrumble API',
        //documentation: 'https://drive.google.com/open?id=1Q7M9immTpeOPTIU5YpzBSn9f_k-VgX0Ii9DalNl0xmg',
        //auth: host + '/auth',
        //user: host + '/user',
        //catalog: host + '/catalog'
    });
});

router.post('/login', (req, res) => {
    let query = "select id from sc_user where username = lower(:name) and password = :password",
        param = [req.body.name, req.body.password];

    oracleConnection.execute(query, param)
        .then((result) => {
            if(result.rows.length < 1)
                throw Error("Unauthorized");
            res.json({id: result.rows[0][0]});
        })
        .catch((err) => error.respondWith(res, err));
});

router.post('/register', (req, res) => {
    let query = "insert into sc_user values(null, lower(:name), :password)",
        param = [req.body.name, req.body.password];

    oracleConnection.execute(query, param)
        .then(() => {
            query = "select max(id) from sc_user order by id",
                param = [];

            oracleConnection.execute(query, param)
                .then((result) => res.json({id: result.rows[0][0]}))
                .catch((err) => error.respondWith(res, err));
        })
        .catch((err) => error.respondWith(res, err));
});
module.exports = router;