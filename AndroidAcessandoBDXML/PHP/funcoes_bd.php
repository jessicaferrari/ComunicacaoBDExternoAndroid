<?php
	//Função que conecta no banco de dados
	function conecta()
	{
		//Variaveis de apoio da procedure Conecta
		$banco = "nome_do_banco";//Nome do banco de dados utilizado
		$usuario = "usuario_do_banco";//Nome do usuario do banco de dados (nao use o root)
		$senha = "senha_do_banco";//Senha do banco de dados (sempre use um tipo de seguranca ao usar senhas!!)
		$hostname = "servidor";//Host onde se encontra o banco de dados
		
		//A mysql_connect cria uma conexao no banco de dados utilizando as informacoes passadas nas variaveis anteriores
		$conn = mysql_connect($hostname,$usuario,$senha); 
		
		//Faz um teste para ver se de fato o banco utilizado esta online
		mysql_select_db($banco) or die( "Não foi possível conectar ao banco MySQL");
		
		//Verifica se a conexao com os dados utilizados foi bem sucessida
		if (!$conn)//Verifica se NAO conectou
		{
			echo "Não foi possível conectar ao banco MySQL.";//Se nao conectou, passa erro para o usuario
			exit;//Mata a conexao
		}
	}
	
	/*
		Função que faz select no banco de dados
		@param string $campos Campos que serao trazidos na query
		@param string $tabela Tabelas que serao trazidas na query
		@param string $where Clausulas que serao trazidos na query
		@param string $options Opcoes que serao trazidos na query como (rownum, limit, order by, etc)
		@return string Com os dados do banco de dados
	*/
	function select($campos, $tabela, $where, $options)
	{
		conecta();//conecta no banco de dados
		
		//Monta a query com os dados passados por parametro
		$query = "SELECT ".$campos." 
				    FROM ".$tabela." 
				 ".$where." 
				 ".$options;
				
		return mysql_query($query);//Roda a query e retorna seu respectivo valor
	}
?>