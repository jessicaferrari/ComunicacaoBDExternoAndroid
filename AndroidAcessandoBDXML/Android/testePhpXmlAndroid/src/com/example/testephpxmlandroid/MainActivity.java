package com.example.testephpxmlandroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*
		 * Eh extremamente necessario adicionar essas duas linhas!!!
		 * Elas dao permissao para criar, escrever em arquivos
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		/*
		 * Recupera as Labels que serao setadas de acordo com sua respectiva situacao 
		*/
		final TextView txtSP = (TextView) findViewById(R.id.txtSituacaoSP);
		final TextView txtBA = (TextView) findViewById(R.id.txtSituacaoBA);
		final TextView txtRJ = (TextView) findViewById(R.id.txtSituacaoRJ);
		
		//Recupera botao Processar do form
		Button btnProcessar = (Button) findViewById(R.id.btnProcessar);
		
		//Seta o metodo onClick do Botao Processar
		btnProcessar.setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View arg0)
			{
				//Cria um HashMap para receber os valores extraidos do XML e posteriormente popular a tabela 
				HashMap estadosSituacoes = new HashMap<String, String>();
				
				/*
				 * O metodo baixaArquivo, ira baixar o arquivo do site e retornar o mesmo
				 * para o metodo dadosXML, ja este metodo ira ler e varrer o XML e retornara um HashMap com os
				 * respectivos valores dos estados
				 */
				estadosSituacoes = dadosXML(baixaArquivo());
				
				Log.d("FileManager", "vai ler o hashmap");
				
				//Este metodo obtem uma view dos dados mapeados dentro do HashMap
				Set set = estadosSituacoes.entrySet();
				
				//Criamos um iterador para varrer o HashMap
				Iterator i = set.iterator();
				
				//Varremos os valores do HashMap e entao inserimos dentro do grid
				while(i.hasNext())
				{
					Log.d("FileManager", "while");
					//Pega posicao atuacao do HashMap
					Map.Entry entrada = (Map.Entry)i.next();
					
					//Verifica qual o estado para ai setar sua respectivo situacao
					if(entrada.getKey().equals("Sao Paulo"))
					{
						//Seta a Situacao do Estado
						txtSP.setText(entrada.getValue().toString());
					}
					else 
					if(entrada.getKey().equals("Rio de Janeiro"))
					{
						//Seta a Situacao do Estado
						txtRJ.setText(entrada.getValue().toString());
					}
					else 
					if(entrada.getKey().equals("Bahia"))
					{
						//Seta a Situacao do Estado
						txtBA.setText(entrada.getValue().toString());
					}	
					//Cria log para conseguir visualizar o que esta sendo impresso
					Log.d("FileManager", "estado : "+entrada.getKey()+" - Situacao: "+entrada.getValue());
				}
			}
		});
	}
	
	/*
	 * Este metodo faz o download do arquivo XML e o retorna por paremtro
	 */
	private File baixaArquivo() 
	{
		/*
		 * Primeiramente, exclui qualquer arquivo chamado arquivo.txt na pasta do aplicativo que fica
		 * dentro do SDCard do dispositivo. 
		 * Pois, caso ja haja algum outro arquivo com esse mesmo nome, o aplicativo apenas iria escrever dentro desse arquivo
		 * independente se houvesse algo dentro ou nao. Ou seja, alem do conteudo do arquivo anterior, teriamos tambem o conteudo do 
		 * xml que vamos baixar, e isso ocaciona erro no processo de parse de xml
		 */
		new File(this.getExternalFilesDir(null),"arquivo.txt").delete();
		
		//Cria um arquivo TXT onde serao escritos as tags varridas do download posteriormente
		//o this.getExternalFilesDir(null) indica que o arquivo deve ser salvo no SDCard
		File file = new File(this.getExternalFilesDir(null),"arquivo.txt");
		

		//Eh importante usar um bloco try catch para pegarmos erros de acesso de arquivos e parse nesse ponto
		try 
		{
			//Cria um URL que possui o caminho do Site onde o download sera forcado pelo PHP - Voce pode alterar de acordo com o endereco que voce for usar
			URL url = new URL("sua_url"); 
			
			//Criamos logs para o logcat, assim fica muito mais facil de identificarmos possiveis erros 
			Log.d("FileManager", "Fazendo Download...");
			Log.d("FileManager", "Arquivo da URL:" + url+" carregado");			
			
			//Abre uma conexao com a URL criada a cima
			URLConnection ucon = url.openConnection();

			//Cria InputStream onde serao carregados o conteudo do Download 
			InputStream is = ucon.getInputStream();

			//Eh criado um Buffer para que seja possivel carregar byte a byte posteriormente dos dados do arquiv
			BufferedInputStream bis = new BufferedInputStream(is);
			
			//Eh criado um array de Bytes  
			ByteArrayBuffer baf = new ByteArrayBuffer(50);

			//Cria um contador para o while a baixo
			int current = 0;
			
			//Eh criado um while para ler caracter a caracter do arquivo baixado pelo app
			while ((current = bis.read()) != -1) 
			{
				//Adiciona ao Array de Bytes os bytes lidos no arquivo
				baf.append((byte) current);
			}
			
			/*
			 * Apos ler byte a byte e colocalos na memoria, os dados carregados na memorias sao transferidos para
			 * o arquivo file criado anteriormente
			 */
			FileOutputStream fos = new FileOutputStream(file, true);//Cria arquivo
			fos.write(baf.toByteArray());//Escreve dados no arquivo
			fos.close();//Fecha o arquivo
			
			//Eh criado um log para identificar que o download do arquivo foi realizado com sucesso
			Log.d("FileManager", "Arquivo salvo com sucesso em "+this.getExternalFilesDir(null));
		} 
		catch (IOException e) 
		{
			//Imprime log caso haja algum erro
			Log.d("FileManager", "Error: " + e);
			//Imprime erro para o usuario
			Toast.makeText(this, "Erro: "+e, Toast.LENGTH_LONG).show();
		}
		
		//Retorna o arquivo gerado
		return file;	
	}
	
	
	/*
	 * Este metodo recebe um arquivo XML, varre-o e retira seus dados de especificas TAGS 
	 * como uma HashMap com os dados das respetivas cidades
	 */
	public HashMap dadosXML(File inputStream)
	{
		//Cria um Array de duas posicoes onde sera colocado as informacoes dos estados e suas respectivas situacoes
		HashMap<String, String> estadosSituacoes = new HashMap();
		
		//Cria uma variavel que gera construtor de documentos
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try 
		{
			//Cria uma variavel que constroi documentos
		    DocumentBuilder dBuilder = dbf.newDocumentBuilder(); 
		    //Captura do Stream o XML e converte em Documento
		    Document doc = dBuilder.parse(inputStream);
		    //Captura do XML a primeira TAG e seus filhos e joga na variavel Element
		    Element docElement = doc.getDocumentElement();
		    //Carrega todos os filhos da primeira TAG do XML que carregamos antes
		    NodeList list = docElement.getChildNodes();
		    
		    //Contador auxiliar para adicionar o valor no array
		    int y = 0;
		    
		    
		    //Varre as Tags filhas do XML identificando seus respectivos itens
		    for (int i = 0; i < list.getLength(); i++) 
		    {
		    	//Cria uma variavel do tipo Item de uma tag filha
		        Node item = list.item(i);
		        
		        //Verifica se esse item eh de fato filho do nosso elemento pai
		        if (item instanceof Element) {
		        	Element element = (Element) item;								
					
					//Adiciona o valor capturado do no, o nome do Estado e sua Situacao
					estadosSituacoes.put(getValue(element, "nome"), getValue(element, "situacao"));
					Log.d("FileManager", "inseriu");
				}
		    }
		  }
		  catch(FileNotFoundException e)//Indica erro caso nao encontre o arquiv
		  {
			//Envia mensagem de erro
		    Toast.makeText(getApplicationContext(), "Nao foi possivel localizar o arquivo XML. Erro: "+e.getMessage(), Toast.LENGTH_LONG).show();
		    //Cria log
		    Log.d("FileManager", "Nao foi possivel localizar o arquivo XML. Erro :"+e);
		  } 
		  catch (SAXException e)//Indica erro ao converter o XML para SAX
		  {
		 	 //Envia mensagem de erro
			 Toast.makeText(getApplicationContext(), "Nao foi possivel converter o arquivo XML para SAX. Erro: "+e.getMessage(), Toast.LENGTH_LONG).show();
 			 //Cria log
			 Log.d("FileManager", "Nao foi possivel converter o arquivo XML para SAX. Erro :"+e);
		  } 
		  catch (IOException e)//Indica erro de IO
		  {
			//Envia mensagem de erro
			Toast.makeText(getApplicationContext(), "Erro de IO: "+e.getMessage(), Toast.LENGTH_LONG).show();
			//Cria log
			Log.d("FileManager", "Erro de IO. Erro :"+e);
		  } 
		  catch (ParserConfigurationException e) //Indica erro de Parsear o XML
		  {
		    //Envia mensagem de erro
			Toast.makeText(getApplicationContext(), "Nao foi possivel Parsear o arquivo XML. Erro: "+e.getMessage(), Toast.LENGTH_LONG).show();
			//Cria log
			Log.d("FileManager", "Nao foi possivel Parsear o arquivo XML. Erro :"+e);
		  }
		 
		 //Deleta o arquivo para nao dar conflito
		 new File(this.getExternalFilesDir(null),"arquivo.txt").delete();
		 
		 //Retorna os valores extraidos do XML por um HashMap
		 return estadosSituacoes;
	}
	
	/*
	 * Este metodo retorna o valor do No(Elemento) passado por parametro
	 */
	public String getValue(Element item, String name) 
	{
		//Pega o No 
        NodeList nodes = item.getElementsByTagName(name);
        
        //Passa para o metodo getTextNodeValue() o no, e este devolve seu valor interno
        return this.getTextNodeValue(nodes.item(0));
    }
	
	/*
	 * Este metodo pega o No passado por parametro, varre seus filhos e devolve seu valor final
	 */
	private final String getTextNodeValue(Node node) 
	{
        //Cria um no filho
		Node child;
        
        //Verifica se o no passado por parametro nao esta vazio
        if (node != null) 
        {
        	//Verifica se o no possui filhos
            if (node.hasChildNodes()) 
            {
                //Se sim, o no filho criado, recebe o primeiro no filho do no pai
            	child = node.getFirstChild();
            	
            	//Enquanto o no filho possuir tags internas
                while(child != null) 
                {
                	//Verifica se o no filho é do tipo TEXT_NODE, ou seja, se possui texto como valor interno e nao nos filhos
                    if (child.getNodeType() == Node.TEXT_NODE) 
                    {
                    	//Se sim, retorna o valor do texto do no filho
                        return child.getNodeValue();
                    }
                    
                    //Adiciona para o no filho, a proxima tag
                    child = child.getNextSibling();
                }
            }
        }

        //Caso nao haja nos internos, é retornado null
        return "";
    }
}
