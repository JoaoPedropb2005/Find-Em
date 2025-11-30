package com.example.findem

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            FindEmTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    MainPage(
//                        modifier = Modifier.padding(innerPadding),
//                        onContinueClick = {
//                            startActivity(Intent(this, Explication1::class.java))
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun MainPage(
//    modifier: Modifier = Modifier,
//    onContinueClick: () -> Unit
//) {
//    Column(
//        modifier = modifier
//            .padding(16.dp)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.findem_logo),
//            contentDescription = "Logo FindEm",
//            modifier = Modifier
//                .padding(bottom = 24.dp)
//                .size(150.dp)
//        )
//
//        Text(
//            text = "Bem-vindo(a) ao FindEm!",
//            fontSize = 30.sp
//        )
//
//        Button(
//            modifier = Modifier.padding(top = 24.dp),
//            onClick = onContinueClick
//        ) {
//            Text("Continuar")
//        }
//    }
//}
