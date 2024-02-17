// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// set up EJS
app.set('view engine', 'ejs');

// import the Waste class from Waste.js
var Waste = require('./Waste.js');

// import the ClassWaste class from ClassWaste.js
var ClassWaste = require('./ClassWaste.js');

/*
//import the SubClassWaste class from SubClassWaste.js
var SubClassWaste = require('./SubClassWaste.js');
*/

var TreeNode = require('./TreeNode.js');
const Requests = require('./Requests.js');

//import Facility.js
var Facility = require('./Facility.js');

// endpoint to test if the website is online
app.use('/test', (req, res) => {
	// create a JSON object
	var data = { 'message' : 'It works!' };
    // send it back
	res.json(data);
    });

// endpoint for listing all wastes
app.use('/allWastes', (req, res) => {
	Waste.find({})
		.then((wastes) => {
			// render allwastes.ejs
			res.render('allwastes', {'wastes' : wastes})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
})

// endpoint for listing all classes
app.use('/allClasses', (req, res) => {
	ClassWaste.find({})
		.then((classes) => {
			// render allclasses.ejs
			res.render('allclasses', {'classes' : classes})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
})


app.use('/allRequests', (req, res) => {
	Requests.find({})
		.then((requests) => {
			// render allclasses.ejs
			res.render('allrequests', {'requests' : requests})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
})

// endpoint to add a new waste into the database
app.use('/create', (req, res) => {
	// construct the Waste from the form data which is in the request BODY
	var newWaste = new Waste ({
		name: req.body.name,
		nodes: req.body.nodes,
		category: req.body.category
		});

	// write it to the database
	newWaste.save()
		.then((w) => { 
			console.log('successfully added ' + w.name + ' to the database'); 
			// render newwaste.ejs
			res.render('newwaste', {'waste': w})
		} )
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
	});

// endpoint to add a new class
app.use('/createClass', (req, res) => {
	// construct a new class of waste
	var newClass = new ClassWaste ({
		name: req.body.name,
		subclass: req.body.subclass
	});

	newClass.save()
		.then((c) => {
			console.log('successfully added Class ' + c.name + ' to the database');
			// render newclass.ejs
			res.render('newclass', {'classwaste': c})
		})
		.catch((err) => {
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})

	// construct the Waste
	var nameForWaste = req.body.name + " (Class)"
	var nameForNodes = req.body.subclass + " (Sub-Class)"

	var newWaste = new Waste ({
		name: nameForWaste,
		nodes: nameForNodes,
		category: req.body.category
		});

	// write it to the database
	newWaste.save()
		.then((w) => { 
			console.log('successfully added ' + w.name + ' to the database'); 
		} )
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})

	var newSubWaste = new Waste ({
		name: nameForNodes,
		nodes: req.body.nodes,
		category: req.body.category
		});

	// write it to the database
	newSubWaste.save()
		.then((sw) => { 
			console.log('successfully added ' + sw.name + ' to the database'); 
		} )
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
	
	
});

//endpoint creating waste facility contact info
app.use('/addFacility', (req,res) => {
	var newFacility = new Facility ({
		name: req.body.name,
		location: req.body.location,
		phone: req.body.phone,
	});
	
	newFacility.save()
	.then((f) => {
		console.log('successfully added Facility ' + f.name + ' to the database');
		// render newclass.ejs
		res.render('newfacility', {'facility': f})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})
});


//endpoint showing the info for waste facilities
app.use('/allFacility', (req,res) =>{
	Facility.find({})
		.then((facility) => {
			// render facilities.ejs
			res.render('allfacilities', {'facility' : facility})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
});


app.use('/createRequest', (req, res) => {
	// construct the Waste from the form data which is in the request BODY
	var newRequest = new Requests ({
		name: req.body.name,
		nodes: req.body.nodes,
		category: req.body.category,
		feedback: req.body.feedback
		});

	// write it to the database
	newRequest.save()
		.then((f) => { 
			console.log('successfully added ' + f.name + ' to the database'); 
			res.redirect('/allRequests');
		} )
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
});

// endpoint to approve a new request into the database
app.use('/approveRequest', (req, res) => {
	// construct the Waste from the form data which is in the request BODY
	var newWaste = new Waste ({
		name: req.query.name,
		nodes: req.query.nodes,
		category: req.query.category
		});

	// write it to the database
	newWaste.save()
		.then((w) => { 
			console.log('successfully added ' + w.name + ' to the database'); 
			var filter = { 'name' : w.name};
			Requests.deleteOne(filter)
				.then((status) => {
					console.log('successfully deleted ' + req.query.name + ' from the database'); 
				})
				.catch((err) => {
					res.json( { 'status' : err } ); 
				})			
			// render newwaste.ejs
			res.render('newwaste', {'waste': w})
		} )
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
	});


// this one shows the HTML form for this waste
app.use('/showEditForm', (req, res) => {
	var filter = { 'name' : req.query.name };
	// do a query to get the info for this waste
	Waste.findOne(filter)
	.then((w) => {
		// then show the form from the EJS template
		res.render('editform', {'waste' : w})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})
})

// this one shows the HTML form for this ClassWaste
app.use('/showEditFormClass', (req, res) => {
	var filter = { 'name' : req.query.name };
	// do a query to get the info for this person
	ClassWaste.findOne(filter)
	.then((c) => {
		// then show the form from the EJS template
		res.render('editformClass', {'classwaste' : c})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})
})


// this endpoint is called when the user SUBMITS the form to edit a waste
app.use('/edit', (req, res) => {
	// get the name and nodes from the BODY of the request
	var filter = { 'name' : req.body.name };
	var update = { 'nodes' : req.body.nodes, 'category' : req.body.category } ;
	console.log(req.body.category);
	// now update the waste in the database
	Waste.findOneAndUpdate(filter, update)	
	.then((orig) => { // 'orig' refers to the original object before we updated it
		res.render('editedwaste', {'name' : req.body.name,'nodes' : req.body.nodes, 'category' : req.body.category })
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

});

// this endpoint is called when the user SUBMITS the form to edit a class
app.use('/editClass', (req, res) => {
	// get the name and nodes from the BODY of the request
	var filter = { 'name' : req.body.name };
	var update = { 'subclass' : req.body.subclass} ;

	// now update the class in the database
	ClassWaste.findOneAndUpdate(filter, update)	
	.then((orig) => { // 'orig' refers to the original object before we updated it
		res.render('editedclass', {'name' : req.body.name,'subclass' : req.body.subclass})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

});

// endpoint to view the nodes of waste
app.use('/view', (req, res) => {
	var name = req.query.name;

	var filter = {'name' : name};
	
	Waste.findOne(filter)
	.then((result)=>{
		// render viewwaste.ejs
		res.render('viewwaste', {'waste': result, 'requestedWaste': name})	
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

});

// endpoint to view the subclasses of a class
app.use('/viewClass', (req, res) => {
	var name = req.query.name;

	var filter = {'name' : name};
	
	ClassWaste.findOne(filter)
	.then((result)=>{
		// render viewclass.ejs
		res.render('viewclass', {'c': result, 'requestedClass': name})	
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

});

// endpoint to view the requests
app.use('/viewRequest', (req, res) => {
	var name = req.query.name;

	var filter = {'name' : name};
	
	Requests.findOne(filter)
	.then((result)=>{
		res.render('viewrequest', {'r': result, 'requestedRequest': name})	
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

});

// endpoint to view the facilities
app.use('/viewFacility', (req, res) => {
	var name = req.query.name;

	var filter = {'name' : name};
	
	Facility.findOne(filter)
	.then((result)=>{
		res.render('viewfacility', {'f': result, 'requestedFacility': name})	
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

});

function arrayToHashtable(array) {
	const hashtable = {};
  
	array.forEach((value, index) => {
		if (value.nodes.length != 0){
			const nodesArray = value.nodes.split(',').map(item => item.trim());
	  		hashtable[value.name] = {nodeArray: nodesArray,  category: value.category};
		}
		else{
			const nodesArray = [];
	  		hashtable[value.name] = {nodeArray: nodesArray,  category: value.category};
		}
		
	});
  
	return hashtable;
  }

  function breadthFirstSearchTreeGenerator(root, wasteHashtable) {
	if (!root) return;
	var queue = [root];
	while (queue.length > 0) {
	  var currentNode = queue.shift();
	  for (var childinTree of currentNode.children) {
		queue.push(childinTree);
		//console.log("currently we are looking at father:" + childinTree.value);
		if (wasteHashtable.hasOwnProperty(childinTree.value)){
			if (wasteHashtable[childinTree.value].nodeArray.length != 0){
				for (var childinHash of wasteHashtable[childinTree.value].nodeArray){
					//console.log("we find node: " + childinHash);
					var childNode = new TreeNode(childinHash);
					childinTree.addChild(childNode);
				}
			}
			else{
				//console.log("this is a leaf");
			}
			
		}
	  }
	}
  }

// endpoint to view the tree
app.use('/viewTree', (req, res) => { 
	Waste.find({})
	.then((wastesArray) => {
		var wasteHashtable = arrayToHashtable(wastesArray);
		if (wasteHashtable.hasOwnProperty("Head")){
			var rootNode = new TreeNode("Head");
			for (var child of wasteHashtable["Head"].nodeArray){
				var childNode = new TreeNode(child);
				rootNode.addChild(childNode);
			}
			breadthFirstSearchTreeGenerator(rootNode, wasteHashtable);
			//console.log(rootNode);
		}
		else{
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		}
		/*
		for (const key in wasteHashtable) {
			if (wasteHashtable.hasOwnProperty(key)) {
			  const value = wasteHashtable[key];
			  console.log(`Key: ${key}, Value: ${value}`);
			}
		}*/
		res.render('viewtree', {'root' : rootNode});

	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})
	/*
	Waste.findOne(filter)
	.then((result)=>{
		res.render('viewwaste', {'waste': result, 'requestedWame': name})	
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})
	*/
});

// endpoint to delete a waste from the database
app.use('/delete', (req, res) => {
	var filter = { 'name' : req.query.name};

	Waste.deleteOne(filter)
		.then((status) => {
			console.log('successfully deleted ' + req.query.name + ' from the database'); 
		})
		.catch((err) => {
			res.json( { 'status' : err } ); 
		})
	
		res.render('deletedwaste', { 'name' : req.query.name});
})

// endpoint to delete a Facility from the database
app.use('/deleteFacility', (req, res) => {
	var filter = { 'name' : req.query.name};

	Facility.deleteOne(filter)
		.then((status) => {
			console.log('successfully deleted ' + req.query.name + ' from the database'); 
		})
		.catch((err) => {
			res.json( { 'status' : err } ); 
		})
	
		res.render('deletedfacility', { 'name' : req.query.name});
})

// endpoint to delete a class from the database
app.use('/deleteClass', (req, res) => {
	var filter = { 'name' : req.query.name};

	ClassWaste.deleteOne(filter)
		.then((status) => {
			console.log('successfully deleted ' + req.query.name + ' from the database'); 
		})
		.catch((err) => {
			res.json( { 'status' : err } ); 
		})
	
		res.render('deletedclass', { 'name' : req.query.name});
})

// endpoint to delete a request from the database
app.use('/deleteRequest', (req, res) => {
	var filter = { 'name' : req.query.name};
	Requests.deleteOne(filter)
		.then((status) => {
			console.log('successfully deleted ' + req.query.name + ' from the database'); 
			res.redirect('/allRequests');
		})
		.catch((err) => {
			res.json( { 'status' : err } ); 
		})
})

//Here begins the andriod functions
//User User Story #3 
app.use('/userFilesRequest', (req, res) => {
	var requestName = '';
	var requestNodes = '';
	var requestCategory = '';
	var requestFeedback = '';

	console.log(req.query.name);
	console.log(req.query.category);
	console.log(req.query.feedback);

	if (req.query.name.length != 0){
		requestName = req.query.name;
	}
	else{
		requestName = 'user request';
	}
	if (req.query.category.length != 0){
		requestCategory = req.query.category;
	}
	else{
		requestCategory = 'N/A';
	}
	requestFeedback = req.query.feedback;

	var newRequest = new Requests ({
		name: requestName,
		nodes: requestNodes,
		category: requestCategory,
		feedback: requestFeedback
		});

	// write it to the database
	newRequest.save()
		.then((f) => { 
			console.log('success add' + f.name + "in the db");
			var data = { 'message' : 'Successfully added your request to the database'};
			res.send(data);
		} )
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
});


//User User Story #2 
app.use('/search', (req, res) => {
	if (req.query.name == null || req.query.name == '' || !req.query.name){
		var data = { 'message' : 'Please enter a trash name'};
		res.send(data);
		return
	}

	var filter = { 'name' : req.query.name};
	Waste.findOne(filter)
	.then((result)=>{
		var data = { 'message' : result.category };
		res.send(data);
	})
	.catch((err) => {
		filter = { 'name' : req.query.name + ' (Sub-Class)'};
		Waste.findOne(filter)
		.then((result)=>{
			var data = { 'message' : result.category };
			res.send(data);
		})
		.catch((err) => {
			filter = { 'name' : req.query.name + ' (Class)'};
			Waste.findOne(filter)
			.then((result)=>{
				var data = { 'message' : result.category };
				res.send(data);
			})
			.catch((err) => {
				var data = { 'message' : 'This trash is not in the database'};
				res.send(data);
			})
		})
	})
})



/*************************************************
Do not change anything below here!
*************************************************/

app.use('/public', express.static('public'));

// this redirects any other request to the "all" endpoint
app.use('/', (req, res) => { res.redirect('/allWastes'); } );

// this port number has been assigned to your group
var port = 3008

app.listen(port,  () => {
	console.log('Listening on port ' + port);
    });


/*

** Attempt to make a new subclass object **

// endpoint for adding a new subclass
app.use('/createSubClass', (req, res) => {
	var newSubClass = new SubClassWaste ({
		name: req.body.name,
		items: req.body.items
	});

	newSubClass.save()
		.then((sc) => {
			console.log('successfully added subclass ' + sc.name + ' to the database');
			// render newclass.ejs
			res.render('newsubclass', {'subclass': sc})
		})
		.catch((err) => {
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
})
*/

	
