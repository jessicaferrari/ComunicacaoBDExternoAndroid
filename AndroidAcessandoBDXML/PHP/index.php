<?php
	require_once('funcoes_bd.php');//Importa o arquivo que fara acesso ao banco de dados
	
	//Faz a Query com as informações desejadas para montar o XML
	$result = select("nome, situacao", "estados", "", "");
	/*
		Essa query ficaria assim: SELECT nome, situacao FROM estados;
	*/
	
	/*
		----------  Formato do XML que deve ser Criado
		
		<?xml version='1.0'?>
		<estados>
		   <estado>
			  <nome>Sao Paulo</nome>
			  <situacao>verde</situacao>
		   </estado>
		   <estado>
			  <nome>Brasilia</nome>
			  <situacao>amarelo</situacao>
		   </estado>
		   <estado>
			  <nome>Rio de Janeiro</nome>
			  <situacao>vermelho</situacao>
		   </estado>
		</estados>
		
		----OBSERVACAO----
		Cuidado a incluir a tag <?xml version='1.0'?>, pois caso ela esteja errada, ocorrera erro ao ser lido pelo Android posteriormente!!!!
	*/

	//Verifica se ha resultado na query
	if($result == FALSE) 
	{
		echo "Erro ao executar a query!";//Avisa que ocorreu erro ao usuario
		die(mysql_error());//Caso nao haja resultado na query, o sql é morto
	}
	else if($result == "")//Verifica se a query veio fazia
	{
		echo "Nao foi localizado nenhum resultado nesta query!";//Avisa o usuario do erro
	}
	
	//Antes de entrar no while, cria cabecalho do XML - Para isso usasse echo para imprimir no proprio browser
	echo "<?xml version='1.0'?>";
	echo "<estados>";
	
	//Faz um laco para recuperar todos os dados da query
	while($cont = mysql_fetch_array($result))
	{	
		//Como eh um novo estado, deve ser criado uma tag <estado> para cada estado encontrado no select
		echo "<estado>";//Demarca um novo estado
		echo "<nome>".$cont['nome']."<nome />";//Adiciona o nome do estado encontrado na query atraves de '$cont['nome'];'
		echo "<situacao>".$cont['situacao']."<situacao />";//Adiciona o nome do estado encontrado na query atraves de '$cont['situacao'];'
		echo "<estado />";
	}
	
	//Apos terminar de imprimir todos os estados, eh necessario fechar o xml
	echo "<estados />";
	
	/*
		A partir daqui sera criado a parte de codigo que forcara o download do arquivo
	*/	
	$arquivo = "arquivo.html";//Nome do arquivo que sera criado
    if(isset($arquivo) && file_exists($arquivo))// faz o teste se a variavel nao esta vazia e se o arquivo realmente existe
	{ 
	   //verifica a extensao do arquivo para pegar o tipo
       switch(strtolower(substr(strrchr(basename($arquivo),"."),1)))
	   { 
          case "htm": // deixar vazio por seuranca
          case "html": // deixar vazio por seuranca
 	   }
       header("Content-Length: ".filesize($arquivo)); //Informa o tamanho do arquivo ao navegador
       header("Content-Disposition: attachment; filename=".basename($arquivo)); //Informa ao navegador que eh tipo anexo e faz abrir a janela de download, tambem informa o nome do arquivo
       readfile($arquivo); //Le o arquivo
       exit; // aborta pos-acoes
	}
?>